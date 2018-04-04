package com.joefox.servants;

import com.joefox.corba.MonitoringStationPOA;
import com.joefox.corba.Reading;

import java.time.Instant;

class MonitoringStationServer extends MonitoringStationPOA {

    private float currentSensorValue = 0.0;
    private int alertThreshold      = 200;
    private String stationName      = "";
    private String stationLocation  = "";

    private boolean on;

    public MonitoringStationServant(
        String stationLocation,
        String stationName
    ) {
        this.stationName     = stationName;
        this.stationLocation = stationLocation;
    }

    public Reading get_reading () {
        Instant instant = Instant.now();
        return new Reading(
            (float) instant.getEpochSecond(),
            currentSensorValue,
            stationName
        );
    }

    public String get_station_location () {
        return this.stationLocation;
    }

    public String get_station_name () {
        return this.stationName;
    }

    public void reset () {
        this.currentSensorValue = 0;
    }

    public void set_alert_threshold (int threshold) {
        this.alertThreshold = threshold;
    }

    public void turn_off () {
        this.on = false;
    }

    public void turn_on () {
        this.on = true;
    }

    public void setSensorValue (float value) {
        this.currentSensorValue = value;
    }
}
