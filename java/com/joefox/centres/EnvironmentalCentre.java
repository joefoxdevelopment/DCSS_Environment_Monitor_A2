package com.joefox.centres;

import com.joefox.agencies.Agency;
import com.joefox.clients.RegionalCentreClient;
import com.joefox.threads.OrbThread;

import java.util.ArrayList;

import org.omg.CORBA.*;
import org.omg.CosNaming.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

public class EnvironmentalCentre {

    private OrbThread orbThread;

    private ArrayList<Agency> agencies;
    private ArrayList<RegionalCentreClient> regionalCentres;
    private String name;

    public EnvironmentalCentre(String name, String args[]) {
        this.name            = name;
        this.agencies        = new ArrayList<Agency>();
        this.regionalCentres = new ArrayList<RegionalCentreClient>();

        this.bindToNamingService(args);
    }

    private void bindToNamingService(String args[]) {
        try {
            ORB orb = new ORB.init(args, null);

            //Reference POA and activate manager
            POA rootpoa = POAHelper.narrow(
                orb.resolve_initial_references("RootPOA")
            );
            rootpoa.the_POAManager().activate();

            //Get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(
                this.servant
            );
            com.joefox.corba.MonitoringStation msRef =
                MonitoringStationHelper.narrow(ref);

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

            NameComponent[] name = nameService.to_name(this.stationName);
            nameService.rebind(name, msRef);

            this.orbThread = new OrbThread(orb);
            this.orbThread.start();
        } catch (Exception e) {
            System.out.println(String.format(
                "Unable to register the Environmental Centre with the " +
                "naming service.\n%s",
                e.getMessage()
            ));
            System.exit(1);
        }
    }



}
