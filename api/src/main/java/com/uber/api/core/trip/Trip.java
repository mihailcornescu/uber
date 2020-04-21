package com.uber.api.core.trip;

import java.time.LocalDateTime;

public class Trip {
    private int tripId;
    private String startLocation;
    private String endLocation;

    private int driverId;
    private int rate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Trip() {
        tripId = 0;
        startLocation = null;
        endLocation = null;
        driverId = 0;
        startTime = null;
        endTime = null;
    }

    public int getTripId() {
        return tripId;
    }

    public Trip setTripId(int tripId) {
        this.tripId = tripId;
        return this;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public Trip setStartLocation(String startLocation) {
        this.startLocation = startLocation;
        return this;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public Trip setEndLocation(String endLocation) {
        this.endLocation = endLocation;
        return this;
    }

    public int getDriverId() {
        return driverId;
    }

    public Trip setDriverId(int driverId) {
        this.driverId = driverId;
        return this;
    }

    public int getRate() {
        return rate;
    }

    public Trip setRate(int rate) {
        this.rate = rate;
        return this;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Trip setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public Trip setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }
}
