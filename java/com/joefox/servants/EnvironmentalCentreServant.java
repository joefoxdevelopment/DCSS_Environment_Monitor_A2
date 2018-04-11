package com.joefox.servants;

import com.joefox.centres.EnvironmentalCentre;
import com.joefox.corba.EnvironmentalCentrePOA;

/**
 * Implementation of the EnvironmentalCentre defined in the IDL
 *
 * @extends com.joefox.corba.EnvironmentalCentrePOA
 *
 * @author Joe Fox U1454236
 * @version 2018-04-10
 */
public class EnvironmentalCentreServant extends EnvironmentalCentrePOA {

    /**
     * The EnvironmentalCentre object to pass invocations to
     */
    private EnvironmentalCentre centre;

    /**
     * Class constructor
     *
     * @param centre - the EnvironmentalCentre object
     */
    public EnvironmentalCentreServant(EnvironmentalCentre centre) {
        this.centre = centre;
    }

    /**
     * Register a regional centre with the environmental centre
     *
     * @param name - the name of the regional centre to register
     */
    public void register_regional_centre(String name) {
        this.centre.registerRegionalCentre(name);
    }

    /**
     * Raise an alarm with the environmental centre
     *
     * @param regional_centre_name - the name of the regional centre that is
     *                               raising the alarm
     * @param location             - the location where the alarm is raised at
     * @param reading              - the value of the reading
     */
    public void raise_alarm(
        String regional_centre_name,
        String location,
        float reading
    ) {
        this.centre.raiseAlarm(regional_centre_name, location, reading);
    }

}
