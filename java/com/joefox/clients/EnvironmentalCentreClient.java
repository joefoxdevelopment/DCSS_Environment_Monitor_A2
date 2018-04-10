package com.joefox.clients;

import com.joefox.corba.EnvironmentalCentre;
import com.joefox.corba.EnvironmentalCentreHelper;

import org.omg.CORBA.*;
import org.omg.CosNaming.*;

public class EnvironmentalCentreClient {

    /**
     * Store a reference to the EnvironmentalCentre server implementation
     */
    private String centreName;
    private com.joefox.corba.EnvironmentalCentre centre;

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

    public void raiseAlarm(
        String regionalCentreName,
        String location,
        float reading
    ) {
        this.centre.raise_alarm(regionalCentreName, location, reading);
    }

    public void registerRegionalCentre(String regionalCentreName) {
        this.centre.register_regional_centre(regionalCentreName);
    }
}
