package com.uber.api.core.trip;

public class Trip {
    private int tripId;
    private String startLocation;
    private String endLocation;

    public Trip() {
        tripId = 0;
        startLocation = null;
        endLocation = null;
    }

    public Trip(int tripId, String startLocation, String endLocation) {
        this.tripId = tripId;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
    }

    public int getTripId() {
        return tripId;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

}
