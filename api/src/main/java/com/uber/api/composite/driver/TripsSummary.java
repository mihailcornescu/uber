package com.uber.api.composite.driver;

import java.time.LocalDateTime;

public class TripsSummary {
    private int tripId;
    private String startLocation;
    private String endLocation;

    private int rate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TripsSummary() {}

    public int getTripId() {
        return tripId;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public int getRate() {
        return rate;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public TripsSummary setTripId(int tripId) {
        this.tripId = tripId;
        return this;
    }

    public TripsSummary setStartLocation(String startLocation) {
        this.startLocation = startLocation;
        return this;
    }

    public TripsSummary setEndLocation(String endLocation) {
        this.endLocation = endLocation;
        return this;
    }

    public TripsSummary setRate(int rate) {
        this.rate = rate;
        return this;
    }

    public TripsSummary setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public TripsSummary setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }
}
