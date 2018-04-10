package com.joefox.centres;

import com.joefox.agencies.Agency;
import com.joefox.clients.RegionalCentreClient;
import com.joefox.corba.*;
import com.joefox.servants.EnvironmentalCentreServant;
import com.joefox.threads.OrbThread;

import java.util.ArrayList;
import java.util.Scanner;

import org.omg.CORBA.*;
import org.omg.CosNaming.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

public class EnvironmentalCentre {

    private EnvironmentalCentreServant servant;
    private OrbThread orbThread;

    private ArrayList<Agency> agencies;
    private ArrayList<RegionalCentreClient> regionalCentres;
    private String args[];
    private String name;

    public EnvironmentalCentre(String name, String args[]) {
        this.agencies        = new ArrayList<Agency>();
        this.name            = name;
        this.regionalCentres = new ArrayList<RegionalCentreClient>();
        this.servant         = new EnvironmentalCentreServant(this);

        this.bindToNamingService(args);

        this.mainMenu();
    }

    private void bindToNamingService(String args[]) {
        try {
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
            com.joefox.corba.EnvironmentalCentre ecRef =
                EnvironmentalCentreHelper.narrow(ref);

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

            NameComponent[] name = nameService.to_name(this.name);
            nameService.rebind(name, ecRef);

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

    public void registerRegionalCentre(String regCentreName) {
        this.regionalCentres.add(new RegionalCentreClient(
            regCentreName,
            this.args
        ));
    }

    public void raiseAlarm(
        String regCentreName,
        String location,
        float reading
    ) {
        ArrayList<String> toAlert = this.getAgenciesToAlert(location);

        System.out.println(String.format(
            "ALARM! ALARM! ALARM! ALARM! ALARM! ALARM! ALARM! ALARM! ALARM!" +
            "Raised by regional centre: %s\n" +
            "Raised at location: %s\n" +
            "Reading value: %0.2f\n\n" +
            "Agencies to alert, use the provided contact details:\n%s\n" +
            "ALARM! ALARM! ALARM! ALARM! ALARM! ALARM! ALARM! ALARM! ALARM!",
            regCentreName,
            location,
            reading,
            String.join("\n", toAlert)
        ));
    }

    private ArrayList<String> getAgenciesToAlert(String location) {
        ArrayList<String> toAlert = new ArrayList<String>();
        for (Agency agency: this.agencies) {
            if (agency.checkLocation(location)) {
                toAlert.add(agency.toString());
            }
        }
        return toAlert;
    }

    private void mainMenu() {
        int option = 0;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Environmental Centre\n");
            System.out.println("Main Menu - Select an Option");
            System.out.println("1) Register an agency");

            try {
                option = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Input needs to be a valid number\n\n\n\n");
            }

            switch (option) {
                case 1:
                    this.registerAgency(scanner);
                    break;
                default:
                    System.out.println("Enter a valid menu option\n\n\n\n");
            }
        }
    }

    private void registerAgency(Scanner scanner) {
        System.out.println("\n\n");
        System.out.println("Enter the agency name:\n");
        String agencyName = scanner.nextLine();

        System.out.println("Enter the agency's email: \n");
        String email = scanner.nextLine();

        Agency agency = new Agency(agencyName, email);

        System.out.println(
            "Enter the name of any locations the agency needs alerting about"
        );
        System.out.println("Or type stop to finalise this agency");

        String input = "";

        while (true) {
            input = scanner.nextLine();
            if ("stop".equals(input)) {
                break;
            }
            agency.addLocation(input);
        }
    }
}
