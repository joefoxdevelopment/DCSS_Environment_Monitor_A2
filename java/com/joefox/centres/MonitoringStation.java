package com.joefox.centres;

//import com.joefox.clients.RegionalCentreClient;
import com.joefox.servants.MonitoringStationServant;
import com.joefox.threads.MonitoringStationThread;

import java.util.Scanner;

/**
 * Monitoring station class.
 * Contains logic to initialise connections to regional centres and User
 * Interface.
 *
 * @author Joe Fox U1454236
 * @version 2018-04-05
 *
 */
public class MonitoringStation {

    MonitoringStationServant servant;
    MonitoringStationThread thread;

    /**
     * Constructor for MonitoringStation objects.
     * Initialise the servant and bind to naming service and regional centre.
     */
    public MonitoringStation (
        String stationLocation,
        String stationName
    ) {
        this.servant = new MonitoringStationServant(
            stationLocation,
            stationName
        );

        this.bindToNamingService();
        this.registerWithRegionalCentre();
        this.startCheckInThread();

        /*
         * This must be the last operation in the constructor due to the use of
         * a while true loop that only breaks on user input
         */
        this.monitorUserInput();
    }

    /**
     * Bind this MonitoringStationServant to the naming service
     */
    private void bindToNamingService() {
        //TODO we need the naming service details passed through as an arg
    }

    private void startCheckInThread() {
        this.thread = new MonitoringStationThread(this.servant);

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
            System.out.println("Enter a positive numeric value or the word \"exit\"");
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
     * Pass whatever the Regional Centre will need to manage its Monitoring
     * Stations
     */
    private void registerWithRegionalCentre() {
        //TODO
    }
}
