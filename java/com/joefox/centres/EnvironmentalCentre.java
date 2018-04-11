package com.joefox.centres;

import com.joefox.agencies.Agency;
import com.joefox.clients.RegionalCentreClient;
import com.joefox.clients.MonitoringStationClient;
import com.joefox.corba.*;
import com.joefox.servants.EnvironmentalCentreServant;
import com.joefox.threads.OrbThread;

import java.util.ArrayList;
import java.util.Scanner;

import org.omg.CORBA.*;
import org.omg.CosNaming.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

/**
 * Environmental Centre class.
 * Contains user interface and connection to regional centres to initialise
 *
 * @author Joe Fox U1454236
 * @version 2018-04-10
 */
public class EnvironmentalCentre {

    /**
     * The servant to listen for invocations from regional centres
     */
    private EnvironmentalCentreServant servant;

    /**
     * A thread object that contains the servant
     */
    private OrbThread orbThread;

    /**
     * The list of subscribed agencies
     */
    private ArrayList<Agency> agencies;

    /**
     * The list of associated Regional Centres
     *
     * The client objects are stored to add a layer of exception handling
     */
    private ArrayList<RegionalCentreClient> regionalCentres;

    /**
     * The program arguments. This contains some CORBA parameters as well
     */
    private String args[];

    /**
     * the name of this Environmental Centre. To be passed to the naming service
     */
    private String name;

    /**
     * Class constructor
     *
     * @param name - the name of the Environmental Centre
     * @param args - the program arguments
     */
    public EnvironmentalCentre(String name, String args[]) {
        this.agencies        = new ArrayList<Agency>();
        this.args            = args;
        this.name            = name;
        this.regionalCentres = new ArrayList<RegionalCentreClient>();
        this.servant         = new EnvironmentalCentreServant(this);

        this.bindToNamingService(args);
        this.mainMenu();
    }

    /**
     * Bind the servant to the naming service. Start a thread to listen for
     * invocations
     *
     * Derived from Gary Allen's DCSS week 16 lecture notes
     *
     * @param args, the program arguments, contains CORBA parameters
     */
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

    /**
     * Add a regional centre client to the list of regional centres
     *
     * @param regCentreName - the name of the regional centre
     */
    public void registerRegionalCentre(String regCentreName) {
        this.regionalCentres.add(new RegionalCentreClient(
            regCentreName,
            this.args
        ));
    }

    /**
     * Raise and display an alarm.
     *
     * @param regCentreName - the regional centre that raised this alert
     * @param location      - the location the alert is raised at
     * @param reading       - the reading value
     */
    public void raiseAlarm(
        String regCentreName,
        String location,
        float reading
    ) {
        ArrayList<String> toAlert = this.getAgenciesToAlert(location);

        System.out.println(String.format(
            "ALARM! ALARM! ALARM! ALARM! ALARM! ALARM! ALARM! ALARM! ALARM!" +
            "\nRaised by regional centre: %s\n" +
            "Raised at location: %s\n" +
            "Reading value: %s\n\n" +
            "Agencies to alert, use the provided contact details:\n%s\n" +
            "ALARM! ALARM! ALARM! ALARM! ALARM! ALARM! ALARM! ALARM! ALARM!",
            regCentreName,
            location,
            reading,
            String.join("\n", toAlert)
        ));
    }

    /**
     * Return a String list of agencies subscribed to a location
     *
     * @param location - the location to alert agencies about
     * @return the list of Stringified agencies
     */
    private ArrayList<String> getAgenciesToAlert(String location) {
        ArrayList<String> toAlert = new ArrayList<String>();
        for (Agency agency: this.agencies) {
            if (agency.checkLocation(location)) {
                toAlert.add(agency.toString());
            }
        }
        return toAlert;
    }

    /**
     * Main menu for the application GUI. As it is a command line application,
     * Menus are handled using System.out.println() and a java.util.Scanner is
     * bound to System.in to listen to user input.
     */
    private void mainMenu() {
        int option = 0;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nEnvironmental Centre");
            System.out.println("Main Menu - Select an Option");
            System.out.println("1) Register an agency");
            System.out.println("2) View a regional centre's log");
            System.out.println("3) Clear a monitoring stations log");
            System.out.println("4) Turn off a monitoring station");
            System.out.println("5) Turn on a monitoring station");
            System.out.println("6) Reset a monitoring station");
            System.out.println("7) List regional centres");
            System.out.println("8) Poll a regional centre");

            try {
                option = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Input needs to be a valid number\n\n\n\n");
                continue;
            }

            switch (option) {
                case 1:
                    this.registerAgency(scanner);
                    break;
                case 2:
                    this.viewRegionalCentreLog(scanner);
                    break;
                case 3:
                    this.clearRegionalCentreLog(scanner);
                    break;
                case 4:
                    this.turnOffMonitoringStation(scanner);
                    break;
                case 5:
                    this.turnOnMonitoringStation(scanner);
                    break;
                case 6:
                    this.resetMonitoringStation(scanner);
                    break;
                case 7:
                    this.listRegionalCentres();
                    break;
                case 8:
                    this.getRegionalCentreReadings(scanner);
                    break;
                default:
                    System.out.println("Enter a valid menu option\n\n\n\n");
            }
        }
    }

    /**
     * Retrieve agency details from the user and add the agency to the list
     *
     * @param scanner - the input parser
     */
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

        this.agencies.add(agency);
    }

    /**
     * Display the log for an associated regional centre
     *
     * @param scanner - the input parser to read the regional centre name
     */
    private void viewRegionalCentreLog(Scanner scanner) {
        System.out.println(
            "Enter the name of the regional centre's logs to view"
        );

        String name = scanner.nextLine();

        for (RegionalCentreClient client: regionalCentres) {
            if (client.getCentreName().equals(name)) {
                System.out.println(client.getLog());
                return;
            }
        }
        System.out.println("Unable to find a regional centre with that name");
    }

    /**
     * Clear the log of an associated regional centre
     *
     * @param scanner - the input parser to read the regional centre name
     */
    private void clearRegionalCentreLog(Scanner scanner) {
        System.out.println(
            "Enter the name of the regional centre's logs to clear"
        );

        String name = scanner.nextLine();

        for (RegionalCentreClient client: regionalCentres) {
            if (client.getCentreName().equals(name)) {
                client.clearLog();
                return;
            }
        }
        System.out.println("Unable to find a regional centre with that name");
    }

    /**
     * Turn off an active monitoring station
     *
     * @param scanner - the input parser to read the monitoring station name
     */
    private void turnOffMonitoringStation(Scanner scanner) {
        System.out.println(
            "Enter the name of the monitoring station to turn off"
        );

        String                    name = scanner.nextLine();
        MonitoringStationClient client = this.getMonitoringStationClient(name);

        client.turnOff();
    }

    /**
     * Turn on an inactive monitoring station
     *
     * @param scanner - the input parser to read the monitoring station name
     */
    private void turnOnMonitoringStation(Scanner scanner) {
        System.out.println(
            "Enter the name of the monitoring station to turn on"
        );

        String                    name = scanner.nextLine();
        MonitoringStationClient client = this.getMonitoringStationClient(name);

        client.turnOn();
    }

    /**
     * Reset the sensor on a monitoring station
     *
     * @param scanner - the input parser to read the monitoring station name
     */
    private void resetMonitoringStation(Scanner scanner) {
        System.out.println(
            "Enter the name of the monitoring station to reset"
        );

        String                    name = scanner.nextLine();
        MonitoringStationClient client = this.getMonitoringStationClient(name);

        client.reset();

    }

    /**
     * Display a list of all stored regional centres
     */
    private void listRegionalCentres() {
        for (RegionalCentreClient client: regionalCentres) {
            System.out.println(client.getCentreName());
        }
    }

    /**
     * Display all current readings from monitoring stations associated with a
     * connected regional centre
     *
     * @param scanner - the input parser to read the monitoring station name
     */
    private void getRegionalCentreReadings(Scanner scanner) {
        System.out.println(
            "Enter the name of the regional centre to poll for updates"
        );

        String name = scanner.nextLine();

        for (RegionalCentreClient client: regionalCentres) {
            if (client.getCentreName().equals(name)) {
                System.out.println(client.poll());
                return;
            }
        }
        System.out.println("Unable to find a regional centre with that name");
    }

    /**
     * Connect to a monitoring station for power and reset operations
     *
     * @param name - the name of the monitoring station to connect to
     * @return a client to handle connections to the monitoring station
     */
    private MonitoringStationClient getMonitoringStationClient(String name) {
        return new MonitoringStationClient(name, this.args);
    }
}
