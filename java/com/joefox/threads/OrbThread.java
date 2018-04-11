package com.joefox.threads;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import org.omg.CosNaming.*;

/**
 * Class to contain the thread to contain each ORB
 *
 * @extends java.lang.Thread
 *
 * @author Joe Fox U1454236
 * @version 2018-04-08
 */
public class OrbThread extends Thread {

    /**
     * The CORBA orb object
     */
    private ORB orb;

    /**
     * Class constructor
     *
     * @param orb - The ORB object to run
     */
    public OrbThread(ORB orb) {
        this.orb = orb;
    }

    /**
     * Run the ORB
     *
     * Overridden from extended Thread class
     */
    @Override
    public void run() {
        this.orb.run();
    }
}
