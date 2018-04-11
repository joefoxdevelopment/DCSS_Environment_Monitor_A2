package com.joefox.servants;

import com.joefox.corba.MonitoringStationPOA;
import com.joefox.corba.Reading;

import java.time.Instant;

/**
 * Implementation of the MonitoringStation defined in the IDL
 *
 * @extends com.joefox.corba.MonitoringStationPOA
 *
 * @author Joe Fox U1454236
 * @version 2018-04-04
 */
public class MonitoringStationServant extends MonitoringStationPOA {

    private float currentSensorValue = (float) 0.0;
    private int alertThreshold       = 200;
    private String stationLocation   = "";
    private String stationName       = "";

    private boolean on = true;

    /**
     * Class constructor
     */
    public MonitoringStationServant(
        String stationLocation,
        String stationName
    ) {
        this.stationName     = stationName;
        this.stationLocation = stationLocation;
    }

    /*
     * Start of functions required by IDL generated interface
     */

    /**
     * Return the current sensor value in a Reading object
     *
     * @return the reading object with the saved reading value
     */
    public Reading get_reading () {
        if (!this.on) {
            return null;
        }

        Instant instant = Instant.now();
        return new Reading(
            (float) instant.getEpochSecond(),
            this.currentSensorValue,
            this.stationName,
            this.stationLocation
        );
    }

    /**
     * Get the station location
     *
     * @return the location of the station
     */
    public String get_station_location () {
        if (!this.on) {
            return null;
        }

        return this.stationLocation;
    }

    /**
     * Get the station name
     *
     * @return the name of the station
     */
    public String get_station_name () {
        if (!this.on) {
            return null;
        }

        return this.stationName;
    }

    /**
     * Reset the station's sensor value
     */
    public void reset () {
        if (!this.on) {
            return;
        }

        this.currentSensorValue = 0;
    }

    /**
     * Set the monitoring station to be inactive
     */
    public void turn_off () {
        this.on = false;
    }

    /**
     * Set the monitoring station to be active
     */
    public void turn_on () {
        this.on = true;
    }

    /*
     * End of functions required by IDL interface
     */

    /**
     * Set the current sensor value
     * To be called when there is a new value entered on the UI
     *
     * @param value the new value that the sensor will have
     */
    public void setSensorValue (float value) {
        this.currentSensorValue = value;
    }
}
