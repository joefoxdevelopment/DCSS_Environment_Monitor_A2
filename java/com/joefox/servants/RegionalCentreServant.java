package com.joefox.servants;

import com.joefox.centres.RegionalCentre;
import com.joefox.corba.RegionalCentrePOA;
import com.joefox.corba.Reading;

public class RegionalCentreServant extends RegionalCentrePOA {

    private RegionalCentre centre;

    public RegionalCentreServant(RegionalCentre centre) {
        this.centre = centre;
    }

    public void clear_log() {
        System.out.println("clear_log called");
        this.centre.clearLog();
    }

    public String get_current_readings() {
        System.out.println("get_current_readings called");
        return this.centre.getReadings();
    }

    public String get_log() {
        System.out.println("get_log called");
        return this.centre.getLog();
    }

    public void register_monitoring_station (String name) {
        System.out.println("register_monitoring_station called at " + name);
        this.centre.createMonitoringStationClient(name);
    }

    public void submit_reading (Reading reading) {
        this.centre.addReadingToLog(reading);
    }

}
