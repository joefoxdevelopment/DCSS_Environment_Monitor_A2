package com.joefox.runners;

import com.joefox.centres.RegionalCentre;

/**
 *
 */
public class StartRegionalCentre {

    /**
     * Verify args and start monitoring station
     */
    public static void main(String[] args) {
        if (null == args ||
            0 == args.length ||
            null == args[0] ||
            null == args[1] ||
            "".equals(args[0]) ||
            "".equals(args[1])
        ) {
            System.out.println("Command line arguments are required.");
            System.out.println("    Regional Centre Name");
            System.out.println("    Environmental Centre Name");
        }

        RegionalCentre station = new RegionalCentre(
            args[0],
            args[1],
            args
        );
    }
}
