package com.dbc.kiosk;
/**
 * PossibleDestination.java
 *
 * Objects to store in possibleDestinations list of GetPossibleShipTos.java
 * The list will store 6 PossibleDestination objects, 1 of which will have
 * matchesOrder == "True", the other 5 false.
 */
public class PossibleDestination {

    private String SOPNumber;
    private String coolerLocation;
    private String destination;
    private String matchesOrder;

    public PossibleDestination(String SOPNumber, String coolerLocation, String destination, String matchesOrder) {
        this.SOPNumber = SOPNumber;
        this.coolerLocation = coolerLocation;
        this.destination = destination;
        this.matchesOrder = matchesOrder;
    }

    public String getSOPNumber() {
        return SOPNumber;
    }

    public String getCoolerLocation() {
        return coolerLocation;
    }

    public String getDestination() {
        return destination;
    }

    public String getMatchesOrder() {
        return matchesOrder;
    }
}
