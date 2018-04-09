package com.joefox.servants;

import com.joefox.centres.RegionalCentre;
import com.joefox.corba.RegionalCentrePOA;

public class RegionalCentreServant extends RegionalCentrePOA {

    private RegionalCentre centre;

    public RegionalCentreServant(RegionalCentre centre) {
        this.centre = centre;
    }

    public void clear_log() {
        System.out.println("clear_log called");
    }

    public void get_current_readings() {
        System.out.println("get_current_readings called");
    }

    public void get_log() {
        System.out.println("get_log called");
    }

    public void raise_alarm (String location) {
        System.out.println("raise_alarm called at " + location);
    }

    public void register_monitoring_station (String name) {
        System.out.println("register_monitoring_station called at " + name);
        this.centre.createMonitoringStationClient(name);
    }

    public void turn_off_monitoring_station (String name) {
        System.out.println("turn_off_monitoring_station called at " + name);
    }

    public void turn_on_monitoring_station (String name) {
        System.out.println("turn_on_monitoring_station called at " + name);
    }

}
