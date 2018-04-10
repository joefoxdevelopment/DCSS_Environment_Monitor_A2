package com.joefox.runners;

import com.joefox.centres.EnvironmentalCentre;

public class StartEnvironmentalCentre {

    /**
     * Verify args and start centre
     */
    public static void main(String[] args) {
        if (null == args ||
            0 == args.length ||
            null == args[0] ||
            "".equals(args[0])
        ) {
            System.out.println("Command line arguments are required.");
            System.out.println("    Environmental Centre Name");
        }

        EnvironmentalCentre centre = new EnvironmentalCentre(args[0], args);
    }
}
