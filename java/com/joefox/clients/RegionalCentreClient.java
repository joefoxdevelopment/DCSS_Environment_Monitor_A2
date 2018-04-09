package com.joefox.clients;

import com.joefox.corba.*;

import org.omg.CORBA.*;
import org.omg.CosNaming.*;

public class RegionalCentreClient {

    private String centreName;
    private com.joefox.corba.RegionalCentre centre;

    public RegionalCentreClient(String centreName, String args[]) {
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

            centre = RegionalCentreHelper.narrow(
                nameService.resolve_str(this.centreName)
            );

            centre.get_current_readings();
        } catch (Exception e) {
            System.out.println(String.format(
                "Unable to find a regional centre in the naming service \n%s",
                e.getMessage()
            ));
            System.exit(1);
        }

    }

}
