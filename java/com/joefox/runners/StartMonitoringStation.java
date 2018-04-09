package com.joefox.runners;

import com.joefox.centres.MonitoringStation;

/**
 * Class to start main thread of Monitoring Station application
 *
 * @author Joe Fox u1454236
 * @version 2018-04-05
 */
public class StartMonitoringStation {

    /**
     * Verify args and start monitoring station
     */
    public static void main(String[] args) {
        if (null == args ||
            0 == args.length ||
            null == args[0] ||
            null == args[1] ||
            null == args[2] ||
            "".equals(args[0]) ||
            "".equals(args[1]) ||
            "".equals(args[2])
        ) {
            System.out.println("Command line arguments are required.");
            System.out.println("    Monitoring Station Name");
            System.out.println("    Monitoring Station Location");
            System.out.println("    Regional Centre Name");
        }

        MonitoringStation station = new MonitoringStation(
            args[0],
            args[1],
            args[2],
            args
        );
    }
}
