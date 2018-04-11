package com.joefox.clients;

import com.joefox.corba.*;

import org.omg.CORBA.*;
import org.omg.CosNaming.*;

/**
 * Monitoring Station client manages the connection and invokes the server
 *
 * @author Joe Fox U1454236
 * @version 2018-04-09
 */
public class MonitoringStationClient {

    /**
     * The name of the monitoring station connected to
     */
    public String stationName;

    /**
     * The CORBA Monitoring Station instance
     */
    private com.joefox.corba.MonitoringStation station;

    /**
     * Class constructor.
     * Initialises the connection
     *
     * @param stationName - the name of the monitoring stations to connect to
     * @param args        - the program args, has some CORBA params
     */
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
                "Unable to find the station in the naming service \n%s",
                e.getMessage()
            ));
        }
    }

    /**
     * Get the location of the connected station
     *
     * @return the string with the station's location
     */
    public String getStationLocation() {
        try {
            return this.station.get_station_location();
        } catch (Exception e) {
            System.out.println(String.format(
                "Unable to get the stations name \n%s",
                e.getMessage()
            ));
        }
        return "";
    }

    /**
     * Get a current reading from the station
     *
     * @return the current reading in a Reading object
     */
    public Reading getReading() {
        try {
            return this.station.get_reading();
        } catch (Exception e) {
            System.out.println(String.format(
                "Unable to get the stations reading \n%s",
                e.getMessage()
            ));
        }
        return null;
    }

    /**
     * Turn the connected monitoring station on
     */
    public void turnOn() {
        try {
            this.station.turn_on();
        } catch (Exception e) {
            System.out.println(String.format(
                "Unable to turn station on \n%s",
                e.getMessage()
            ));
        }
    }

    /**
     * Turn the connected monitoring station off
     */
    public void turnOff() {
        try {
            this.station.turn_off();
        } catch (Exception e) {
            System.out.println(String.format(
                "Unable to turn station off \n%s",
                e.getMessage()
            ));
        }

    }

    /**
     * Reset the sensor value at the connected monitoring station
     */
    public void reset() {
        try {
            this.station.reset();
        } catch (Exception e) {
            System.out.println(String.format(
                "Unable to reset station sensor \n%s",
                e.getMessage()
            ));
        }
    }

}
