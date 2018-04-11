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

/**
 * Regional centre class
 * Manages connected monitoring stations and the connection to the parent
 * Environmental Centre
 *
 * @author Joe Fox U1454236
 * @version 2018-04-09
 */
public class RegionalCentre {

    /**
     * Environmental Centre client to handle connections to the parent
     * environmental centre
     */
    private EnvironmentalCentreClient envCentreClient;

    /**
     * Thread object to contain the servant while listening to be called
     */
    private OrbThread servantThread;

    /**
     * The regional centre servant
     */
    private RegionalCentreServant servant;

    /**
     * List of connected Monitoring Stations
     */
    private ArrayList<MonitoringStationClient> monitoringStations;

    /**
     * List of string entries to the log. Currently only alert level readings
     * are logged
     */
    private ArrayList<String> log;

    /**
     * The program arguments, contains some CORBA parameters
     */
    private String args[];

    /**
     * The name of this regional centre
     */
    private String centreName;

    /**
     * The name of the parent environmental centre
     */
    private String envCentreName;

    /**
     * The alert threshold, currently at 200.0. This value is obtained from the
     * European commision at:
     * http://ec.europa.eu/environment/air/quality/standards.htm
     */
    public static final float ALERT_THRESHOLD = (float) 200.0;

    /**
     * Class constructor
     *
     * @param centreName    - the name of this regional centre
     * @param envCentreName - the name of the parent environmental centre
     * @param args          - the program argument, has some CORBA params
     */
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


    /**
     * Bind this RegionalCentreServant to the naming service. Initialise the
     * ORB and start a new thread for the servant to listen for invocations.
     *
     * Derived from examples in Gary Allen's DCSS week 16 lecture
     *
     * @param args - the program args, contains CORBA arguments
     */
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

    /**
     * Create a client for the monitoring station
     *
     * @param the name of the monitoring station to connect to
     */
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

    /**
     * Add a reading to the log, particularly if it's above the threshold
     *
     * @param reading - the Reading object with the reading details
     */
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

    /**
     * Verify an alert by force checking all sensors at a location
     * If multiple sensors report the high reading, return true
     *
     * @param location - the location to verify the alert
     * @return whether the alert is valid
     */
    private boolean verifyAlert(String location) {
        int aboveThresholdValues = 0;
        for (MonitoringStationClient station: monitoringStations) {
            if (location.equals(station.getStationLocation())) {
                if (this.ALERT_THRESHOLD <= station.getReading().value) {
                    aboveThresholdValues++;
                }
            }
        }
        return aboveThresholdValues > 1;
    }

    /**
     * Return the log in a string format to display.
     *
     * As each entry in the log is already a string,
     * return String.join("\n", this.log); would suffice
     *
     * @return the log in string format
     */
    public String getLog() {
        String logString = "";
        for (String entry: this.log) {
            logString = logString + "\n" + entry;
        }
        return logString;
    }

    /**
     * Remove all entries from the log
     */
    public void clearLog() {
        this.log.clear();
    }

    /**
     * Get a String containing all current readings from each monitoring station
     *
     *  @return the string containing all current readings
     */
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

    /**
     * Convert a reading into a user readable String
     *
     * @param - the reading to format
     * @return the stringified reading
     */
    private String stringifyReading(Reading reading) {
        String stringified = String.format(
            "Reading from %s %s, value: %s, taken at %s",
            reading.station_name,
            reading.station_location,
            reading.value,
            Instant.ofEpochSecond((long) reading.timestamp).toString()
        );

        if (this.ALERT_THRESHOLD <= reading.value) {
            stringified = (char)27 + "[31m" + stringified + (char)27 + "[0m";
        }
        return stringified;
    }
}
