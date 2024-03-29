package com.joefox.centres;

import com.joefox.clients.RegionalCentreClient;
import com.joefox.corba.*;
import com.joefox.servants.MonitoringStationServant;
import com.joefox.threads.OrbThread;
import com.joefox.threads.MonitoringStationUpdateThread;

import java.util.Scanner;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import org.omg.CosNaming.*;

/**
 * Monitoring station class.
 * Contains logic to initialise connections to regional centres and User
 * Interface.
 *
 * @author Joe Fox U1454236
 * @version 2018-04-05
 */
public class MonitoringStation {

    private MonitoringStationServant servant;
    private MonitoringStationUpdateThread thread;
    private OrbThread servantThread;
    private RegionalCentreClient client;

    private String regionalCentre;
    private String stationLocation;
    private String stationName;

    /**
     * Constructor for MonitoringStation objects.
     * Initialise the servant and bind to naming service and regional centre.
     *
     * @param stationName     - the unique name of this station,
     *                          to be registered with the naming service
     * @param stationLocation - the location of this station
     * @param regionalCentre  - the name of the regional centre to connect to in
     *                          the naming service
     * @param args            - the program args, containing CORBA parameters
     */
    public MonitoringStation (
        String stationName,
        String stationLocation,
        String regionalCentre,
        String args[]
    ) {
        this.regionalCentre  = regionalCentre;
        this.stationLocation = stationLocation;
        this.stationName     = stationName;

        this.client = new RegionalCentreClient(regionalCentre, args);

        this.servant = new MonitoringStationServant(
            stationLocation,
            stationName
        );

        this.bindToNamingService(args);
        this.registerWithRegionalCentre();
        this.startCheckInThread();

        /*
         * This must be the last operation in the constructor due to the use of
         * a while true loop that only breaks on user input
         */
        this.monitorUserInput();
    }

    /**
     * Bind this MonitoringStationServant to the naming service. Initialise the
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
            com.joefox.corba.MonitoringStation msRef =
                MonitoringStationHelper.narrow(ref);

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

            NameComponent[] name = nameService.to_name(this.stationName);
            nameService.rebind(name, msRef);

            this.servantThread = new OrbThread(orb);
            this.servantThread.start();
        } catch ( Exception e ) {
            System.out.println(String.format(
                "Unable to register the Monitoring Station with the " +
                "naming service.\n%s",
                e.getMessage()
            ));
            System.exit(1);
        }
    }

    /**
     * Start a thread to automatically post updates to the named regional centre
     */
    private void startCheckInThread() {
        this.thread = new MonitoringStationUpdateThread(this.servant, this.client);
        this.thread.start();
    }

    /**
     * Method to listen for user input to set the sensor values
     */
    private void monitorUserInput() {
        String input     = "";
        Scanner scanner  = new Scanner(System.in);
        float val        = 0;
        boolean closeApp = false;

        while (!closeApp) {
            System.out.println(
                "Enter a positive numeric value or the word \"exit\""
            );
            input = scanner.nextLine();

            if ("exit".equals(input)) {
                System.out.println("Closing Monitoring Station");
                closeApp = true;
                this.thread.interrupt();
                continue;
            }

            try {
                val = Float.parseFloat(input);

                if (val < 0) {
                    throw new NumberFormatException();
                }

                this.servant.setSensorValue(val);
            } catch (NumberFormatException e) {
                System.out.println("Input must be a positive number");
                continue;
            }
        }
    }

    /**
     * Pass the station name so that the regional centre can create a client for
     * this monitoing station
     */
    private void registerWithRegionalCentre() {
        this.client.registerMonitoringStationWithRegionalCentre(
            this.stationName
        );
    }
}
