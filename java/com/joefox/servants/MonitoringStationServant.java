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

    private boolean on;

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
            this.stationName
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


    public String get_station_name () {
        if (!this.on) {
            return null;
        }

        return this.stationName;
    }

    public void reset () {
        if (!this.on) {
            return;
        }

        this.currentSensorValue = 0;
    }

    public void set_alert_threshold (int threshold) {
        if (!this.on) {
            return;
        }

        this.alertThreshold = threshold;
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

    /**
     * Use the RegionalCentreClient to send updated readings to the regional
     * centre
     *
     * @param reading the Reading object to send
     */
    public void sendReadingToRegionalCentre (Reading reading) {
        //TODO after client is implemented
    }
}
