package com.joefox.threads;

import com.joefox.clients.RegionalCentreClient;
import com.joefox.corba.Reading;
import com.joefox.servants.MonitoringStationServant;

/**
 * Class to contain the thread for the MonitoringStation's automatic polling
 * functionality
 *
 * @extends java.lang.Thread
 *
 * @author Joe Fox U1454236
 * @version 2018-04-05
 */
public class MonitoringStationUpdateThread extends Thread {

    private MonitoringStationServant servant;
    private RegionalCentreClient client;

    /**
     * Class constructor
     *
     * @param servant - the MonitoringStationServant to get readings from
     * @param client  - the regional centre client to post updates to
     */
    public MonitoringStationUpdateThread(
        MonitoringStationServant servant,
        RegionalCentreClient client
    ) {
        this.client  = client;
        this.servant = servant;
    }

    /**
     * Run the update loop to parse user input and send to regional centre
     *
     * Overridden from extended Thread class
     */
    @Override
    public void run() {
        Reading reading;
        boolean endThread = false;

        System.out.println("Starting Monitoring Station Update Thread");

        while (!endThread) {
            reading = this.servant.get_reading();

            if (null != reading) {
                this.client.sendReadingToRegionalCentre(reading);
            }

            try {
                sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("Ending Monitoring Station Update Thread");
                endThread = true;
            }
        }
    }
}
