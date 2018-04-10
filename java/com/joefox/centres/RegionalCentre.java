package com.joefox.centres;

import com.joefox.clients.EnvironmentalCentreClient;
import com.joefox.clients.MonitoringStationClient;
import com.joefox.corba.*;
import com.joefox.servants.RegionalCentreServant;
import com.joefox.threads.OrbThread;

import java.time.Instant;
import java.util.ArrayList;

import org.omg.CORBA.*;
import org.omg.CosNaming.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

public class RegionalCentre {

    private EnvironmentalCentreClient envCentreClient;
    private OrbThread servantThread;
    private RegionalCentreServant servant;

    private ArrayList<MonitoringStationClient> monitoringStations;
    private ArrayList<String> log;
    private String args[];
    private String centreName;
    private String envCentreName;

    public static final float ALERT_THRESHOLD = (float) 200.0;

    public RegionalCentre(
        String centreName,
        String envCentreName,
        String args[]
    ) {
        this.args               = args;
        this.centreName         = centreName;
        this.envCentreName      = envCentreName;
        this.log                = new ArrayList<String>();
        this.monitoringStations = new ArrayList<MonitoringStationClient>();
        this.servant            = new RegionalCentreServant(this);
        this.envCentreClient    = new EnvironmentalCentreClient(
            envCentreName,
            args
        );

        System.out.println(
            "Started regional centre, listening for invocations"
        );

        this.bindToNamingService(args);
        this.envCentreClient.registerRegionalCentre(this.centreName);
    }

    private void bindToNamingService(String args[]) {
        try {
            //Init ORB
            ORB orb = ORB.init(args, null);

            //Reference POA and activate manager
            POA rootpoa = POAHelper.narrow(
                orb.resolve_initial_references("RootPOA")
            );
            rootpoa.the_POAManager().activate();

            //Get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(
                this.servant
            );
            com.joefox.corba.RegionalCentre rcRef =
                RegionalCentreHelper.narrow(ref);

            //Get a reference from the naming service
            org.omg.CORBA.Object nameServiceObj =
                orb.resolve_initial_references("NameService");
            if (null == nameServiceObj) {
                throw new Exception("Null nameServiceObj");
            }

            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            NamingContextExt nameService = NamingContextExtHelper.narrow(
                nameServiceObj
            );
            if (null == nameService) {
                throw new Exception("null nameService");
            }

            NameComponent[] name = nameService.to_name(this.centreName);
            nameService.rebind(name, rcRef);

            this.servantThread = new OrbThread(orb);
            this.servantThread.start();
        } catch ( Exception e ) {
            System.out.println(String.format(
                "Unable to register the Regional Centre with the " +
                "naming service.\n%s",
                e.getMessage()
            ));
            System.exit(1);
        }
    }

    public void createMonitoringStationClient(String stationName) {
        try {
            MonitoringStationClient msClient = new MonitoringStationClient(
                stationName,
                this.args
            );

            if (null == msClient) return;

            this.monitoringStations.add(msClient);
        } catch (Exception e) {
            System.out.println(String.format(
                "Unable to create a client for the monitoring station \n%s",
                e.getMessage()
            ));
        }
    }

    public void addReadingToLog(Reading reading) {
        String readingString = this.stringifyReading(reading);

        System.out.println(readingString);

        if (this.ALERT_THRESHOLD <= reading.value &&
            this.verifyAlert(reading.station_location)
        ) {
            this.log.add(String.format(
                "Confirmed high reading at %s",
                reading.station_location
            ));
            System.out.println("High reading confirmed, passing to env centre");
            this.envCentreClient.raiseAlarm(
                this.centreName,
                reading.station_location,
                reading.value
            );
        }
    }

    private boolean verifyAlert(String location) {
        int aboveThresholdValues = 0;

        for (MonitoringStationClient station: monitoringStations) {
            if (location.equals(station.getStationLocation())) {
                if (200 <= station.getReading().value) {
                    aboveThresholdValues++;
                }
            }
        }

        return aboveThresholdValues > 1;
    }

    public String getLog() {
        String logString = "";

        for (String entry: this.log) {
            logString = logString + "\n" + entry;
        }

        return logString;
    }

    public void clearLog() {
        this.log.clear();
    }

    public String getReadings() {
        String readingsString = "";
        Reading reading;

        for (MonitoringStationClient client: monitoringStations) {
            readingsString = readingsString + "\n" + this.stringifyReading(
                client.getReading()
            );
        }

        return readingsString;
    }

    private String stringifyReading(Reading reading) {
        String stringified = String.format(
            "Reading from %s %s, value: %s, taken at %s",
            reading.station_name,
            reading.station_location,
            reading.value,
            Instant.ofEpochSecond((long) reading.timestamp).toString()
        );

        if (200 <= reading.value) {
            stringified = (char)27 + "[31m" + stringified + (char)27 + "[0m";
        }

        return stringified;
    }
}
