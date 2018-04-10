package com.joefox.agencies;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Agency {

    private ArrayList<String> locations;
    private String name;
    private String email;

    public Agency(String name, String rawEmail) {
        this.name      = name;
        this.locations = new ArrayList<String>();

        Pattern pattern = Pattern.compile(rawEmail);
        Matcher matcher = pattern.matcher("/^.+@.+(\\..+)+$/");

        boolean found = false;

        while (matcher.find()) {
            found = true;
        }

        if (!found) {
            this.email = "No email";
        } else {
            this.email = rawEmail;
        }
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
