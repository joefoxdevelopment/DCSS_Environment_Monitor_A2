package com.joefox.agencies;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Agency {

    private ArrayList<String> locations;
    private String name;
    private String email;

    public Agency(String name, String email) {
        this.email     = email;
        this.locations = new ArrayList<String>();
        this.name      = name;
    }

    public String toString() {
        return String.format(
            "Agency: %s\nEmail: %s\nLocation subscriptions: %s",
            this.name,
            this.email,
            String.join(", ", this.locations)
        );
    }

    public boolean checkLocation(String location) {
        for (String subscribed: locations) {
            if (location.equals(subscribed)) {
                return true;
            }
        }

        return false;
    }

    public void addLocation(String location) {
        locations.add(location);
    }
}
