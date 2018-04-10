package com.joefox.servants;

import com.joefox.centres.EnvironmentalCentre;
import com.joefox.corba.EnvironmentalCentrePOA;

public class EnvironmentalCentreServant extends EnvironmentalCentrePOA {

    private EnvironmentalCentre centre;

    public EnvironmentalCentreServant(EnvironmentalCentre centre) {
        this.centre = centre;
    }

    public void register_regional_centre(String name) {
        this.centre.registerRegionalCentre(name);
    }

    public void raise_alarm(
        String regional_centre_name,
        String location,
        float reading
    ) {
        this.centre.raiseAlarm(regional_centre_name, location, reading);
    }

}
