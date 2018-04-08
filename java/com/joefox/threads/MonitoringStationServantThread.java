package com.joefox.threads;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import org.omg.CosNaming.*;

/**
 * Class to contain the thread for the MonitoringStation's automatic polling
 * functionality
 *
 * @extends java.lang.Thread
 *
 * @author Joe Fox U1454236
 * @version 2018-04-08
 */
public class MonitoringStationServantThread extends Thread {

    private ORB orb;

    /**
     * Class constructor
     *
     * @param servant the MonitoringStationServant to get readings from
     * TODO ADD the REGIONALCENTRECLIENT as a param
     */
    public MonitoringStationServantThread(ORB orb) {
        this.orb = orb;
    }

    @Override
    public void run() {
        this.orb.run();
    }
}
