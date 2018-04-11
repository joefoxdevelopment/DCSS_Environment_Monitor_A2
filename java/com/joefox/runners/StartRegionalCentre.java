package com.joefox.runners;

import com.joefox.centres.RegionalCentre;

/**
 * Runner class for Regional Centre app
 *
 * @author Joe Fox U1454236
 * @version 2018-04-06
 */
public class StartRegionalCentre {

    /**
     * Verify args and start regional centre
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
