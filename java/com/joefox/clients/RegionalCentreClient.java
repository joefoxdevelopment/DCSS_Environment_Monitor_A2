package com.joefox.clients;

import com.joefox.corba.*;

import org.omg.CORBA.*;
import org.omg.CosNaming.*;

/**
 * Regional Centre client initialises connections and posts to server
 *
 * @author Joe Fox U1454236
 * @version 2018-04-10
 */
public class RegionalCentreClient {

    /**
     * Name of the regional centre to connect to
     */
    private String centreName;

    /**
     * Reference to the servant object
     */
    private com.joefox.corba.RegionalCentre centre;

    /**
     * Class constructor
     *
     * @param centreName - the name of the regional centre to connect to
     * @param args       - the program argument, has some CORBA options
     */
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

            this.centre = RegionalCentreHelper.narrow(
                nameService.resolve_str(this.centreName)
            );
        } catch (Exception e) {
            System.out.println(String.format(
                "Unable to find a regional centre in the naming service \n%s",
                e.getMessage()
            ));
            System.exit(1);
        }
    }

    /**
     * Register a monitoring station with the connected regional centre
     *
     * @param name - the name of the monitoring station to connect
     */
    public void registerMonitoringStationWithRegionalCentre(String name) {
        try {
            this.centre.register_monitoring_station(name);
        } catch (Exception e) {
            System.out.println(String.format(
                "Unable to register station with regional centre \n%s",
                e.getMessage()
            ));
            System.exit(1);
        }
    }

    /**
     * Send a reading to the Regional Centre
     *
     * @param reading - the reading to submit
     */
    public void sendReadingToRegionalCentre(Reading reading) {
        try {
            this.centre.submit_reading(reading);
        } catch (Exception e) {
            System.out.println(String.format(
                "Unable to submit reading to regional centre \n%s",
                e.getMessage()
            ));
        }
    }

    /**
     * Retrieve the regional centre's log
     *
     * @return the regional centre's log in string format
     */
    public String getLog() {
        try {
            return this.centre.get_log();
        } catch (Exception e) {
            System.out.println(String.format(
                "Unable to get log from regional centre \n%s",
                e.getMessage()
            ));
        }
        return "No logs available";
    }

    /**
     * Clear the regional centre's log
     */
    public void clearLog() {
        try {
            this.centre.clear_log();
        } catch (Exception e) {
            System.out.println(String.format(
                "Unable to clear log from regional centre \n%s",
                e.getMessage()
            ));
        }
    }

    /**
     * Retrieve this regional centre's name
     */
    public String getCentreName() {
        return this.centreName;
    }

    /**
     * Get the latest set of current readings from the regional centre
     *
     * @return the list of readings in String format
     */
    public String poll() {
        try {
            return this.centre.get_current_readings();
        } catch (Exception e) {
            System.out.println(String.format(
                "Unable to poll regional centre \n%s",
                e.getMessage()
            ));
        }
        return "Unable to poll for updates";
    }
}
