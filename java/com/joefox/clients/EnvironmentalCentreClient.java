package com.joefox.clients;

import com.joefox.corba.EnvironmentalCentre;
import com.joefox.corba.EnvironmentalCentreHelper;

import org.omg.CORBA.*;
import org.omg.CosNaming.*;

/**
 * Environmental Centre client to manage connection and invocations
 *
 * @author Joe Fox U1454236
 * @version 2018-04-10
 */
public class EnvironmentalCentreClient {

    /**
     * Store a reference to the EnvironmentalCentre server implementation
     */
    private String centreName;

    /**
     * The CORBA server reference
     */
    private com.joefox.corba.EnvironmentalCentre centre;

    /**
     * Class constructor. Initialised connection to the server
     */
    public EnvironmentalCentreClient(String centreName, String args[]) {
        this.centreName = centreName;

        try {
            ORB orb = ORB.init(args, null);

            org.omg.CORBA.Object nameServiceObj =
                orb.resolve_initial_references ("NameService");
            if (null == nameServiceObj) {
                throw new Exception("nameServiceObj null");
            }
            NamingContextExt nameService = NamingContextExtHelper.narrow(
                nameServiceObj
            );
            if (null == nameService) {
                throw new Exception("nameService null");
            }

            this.centre = EnvironmentalCentreHelper.narrow(
                nameService.resolve_str(this.centreName)
            );
        } catch (Exception e) {
            System.out.println(String.format(
                "Unable to find a env centre in the naming service \n%s",
                e.getMessage()
            ));
            System.exit(1);
        }
    }

    /**
     * Raise an alarm with the environmental centre
     *
     * @param regionalCentreName - the name of the regional centre sending the
     *                             notification
     * @param location           - the location the alarm was raised at
     * @param reading            - the reading value
     */
    public void raiseAlarm(
        String regionalCentreName,
        String location,
        float reading
    ) {
        this.centre.raise_alarm(regionalCentreName, location, reading);
    }

    /**
     * Register a regional centre with the environmental centre
     *
     * @param regionalCentreName - the name of the regional centre
     */
    public void registerRegionalCentre(String regionalCentreName) {
        this.centre.register_regional_centre(regionalCentreName);
    }
}
