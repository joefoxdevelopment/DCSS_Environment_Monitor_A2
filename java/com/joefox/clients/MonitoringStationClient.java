package com.joefox.clients;

import com.joefox.corba.*;

import org.omg.CORBA.*;
import org.omg.CosNaming.*;

public class MonitoringStationClient {

    public String stationName;
    private com.joefox.corba.MonitoringStation station;

    public MonitoringStationClient(String stationName, String args[]) {
        this.stationName = stationName;

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

            this.station = MonitoringStationHelper.narrow(
                nameService.resolve_str(this.stationName)
            );
        } catch (Exception e) {
            System.out.println(String.format(
                "Unable to find a regional centre in the naming service \n%s",
                e.getMessage()
            ));
        }
    }

}
