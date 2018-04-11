package com.joefox.servants;

import com.joefox.centres.RegionalCentre;
import com.joefox.corba.RegionalCentrePOA;
import com.joefox.corba.Reading;

/**
 * Implementation of the RegionalCentre defined in the IDL
 *
 * As there is no user interaction post initialisation, this is a bit more
 * verbose to highlight when each function has been called
 *
 * @extends com.joefox.corba.RegionalCentrePOA
 *
 * @author Joe Fox U1454236
 * @version 2018-04-09
 */
public class RegionalCentreServant extends RegionalCentrePOA {

    /**
     * Reference to the regional centre to invoke operation on
     */
    private RegionalCentre centre;

    /**
     * Class constructor
     *
     * @param centre - the regional centre
     */
    public RegionalCentreServant(RegionalCentre centre) {
        this.centre = centre;
    }

    /**
     * Clear this regional centre's log
     */
    public void clear_log() {
        System.out.println("clear_log called");
        this.centre.clearLog();
    }

    /**
     * Get a set of current readings from all connected monitoring stations
     *
     * @return the stringified list of readings
     */
    public String get_current_readings() {
        System.out.println("get_current_readings called");
        return this.centre.getReadings();
    }

    /**
     * Get this regional centre's log
     *
     * @return the string with this regional centre's log
     */
    public String get_log() {
        System.out.println("get_log called");
        return this.centre.getLog();
    }

    /**
     * Register a monitoring station with this regional centre
     *
     * @param name - the name of the monitoring station to register
     */
    public void register_monitoring_station (String name) {
        System.out.println("register_monitoring_station called at " + name);
        this.centre.createMonitoringStationClient(name);
    }

    /**
     * Send a reading to be processed and determined for further notification
     *
     * @param reading - the reading to process
     */
    public void submit_reading (Reading reading) {
        this.centre.addReadingToLog(reading);
    }
}
