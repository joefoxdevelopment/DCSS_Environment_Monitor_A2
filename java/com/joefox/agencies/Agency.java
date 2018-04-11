package com.joefox.agencies;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Store the details of an agency to be notified when a location exceeds
 * NOx limits. Provide functionality to check an agency's location when levels
 * are exceeded.
 *
 * @author Joe Fox U1454236
 * @version 2018-04-05
 */
public class Agency {

    /**
     * ArrayList of String locations to be notified about
     */
    private ArrayList<String> locations;

    /**
     * The name of the agency
     */
    private String name;

    /**
     * The email address of the agency contact.
     *
     * NOTE: Due to the lack of validation, this could be another contact
     * method, such as a telephone number or an address.
     */
    private String email;

    /**
     * Class constructor
     *
     * @param name  - the name of the agency
     * @param email - the contact details of the agency
     */
    public Agency(String name, String email) {
        this.email     = email;
        this.locations = new ArrayList<String>();
        this.name      = name;
    }

    /**
     * Format the agency in a user friendly format.
     * For example
     *
     * Agency: AGENCY_NAME
     * Email: AGENCY_EMAIL
     * Location subscriptions: LOCATION_1, LOCATION_2, etc
     */
    public String toString() {
        return String.format(
            "Agency: %s\nEmail: %s\nLocation subscriptions: %s",
            this.name,
            this.email,
            String.join(", ", this.locations)
        );
    }

    /**
     * Check if a specified location is in this agency's subscribed locations
     *
     * @param location - the location to check subscription status of
     * @return whether this agency is subscribed to that location
     */
    public boolean checkLocation(String location) {
        for (String subscribed: locations) {
            if (location.equals(subscribed)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Add a location to this agency's location subscriptions
     *
     * @param location - the location to add to the agency's subscriptions
     */
    public void addLocation(String location) {
        locations.add(location);
    }
}
