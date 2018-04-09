package com.joefox.centres;

import com.joefox.clients.EnvironmentalCentreClient;
import com.joefox.clients.MonitoringStationClient;
import com.joefox.corba.*;
import com.joefox.threads.OrbThread;
import com.joefox.servants.RegionalCentreServant;

import java.util.ArrayList;

import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;
import org.omg.CosNaming.*;

public class RegionalCentre {

    private EnvironmentalCentreClient envCentreClient;
    private OrbThread servantThread;
    private RegionalCentreServant servant;

    private String centreName;
    private String envCentreName;

    public RegionalCentre(
        String centreName,
        String envCentreName,
        String args[]
    ) {
        this.centreName    = centreName;
        this.envCentreName = envCentreName;

        this.envCentreClient = new EnvironmentalCentreClient(envCentreName);

        this.servant = new RegionalCentreServant();

        this.bindToNamingService(args);
    }

    private void bindToNamingService(String args[]) {
        try {
            //Init ORB
            ORB orb = ORB.init(args, null);

            //Reference POA and activate manager
            POA rootpoa = POAHelper.narrow(
                orb.resolve_initial_references("RootPOA")
            );
            rootpoa.the_POAManager().activate();

            //Get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(
                this.servant
            );
            com.joefox.corba.RegionalCentre rcRef =
                RegionalCentreHelper.narrow(ref);

            //Get a reference from the naming service
            org.omg.CORBA.Object nameServiceObj =
                orb.resolve_initial_references("NameService");
            if (null == nameServiceObj) {
                throw new Exception("Null nameServiceObj");
            }

            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            NamingContextExt nameService = NamingContextExtHelper.narrow(
                nameServiceObj
            );
            if (null == nameService) {
                throw new Exception("null nameService");
            }

            NameComponent[] name = nameService.to_name(this.centreName);
            nameService.rebind(name, rcRef);

            this.servantThread = new OrbThread(orb);
            this.servantThread.start();
        } catch ( Exception e ) {
            System.out.println(String.format(
                "Unable to register the Monitoring Station with the " +
                "naming service.\n%s",
                e.getMessage()
            ));
            System.exit(1);
        }
    }

}
