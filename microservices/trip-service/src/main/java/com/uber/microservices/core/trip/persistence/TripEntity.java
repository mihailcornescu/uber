package com.uber.microservices.core.trip.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

import static java.lang.String.format;

@Document(collection="trips")
@CompoundIndex(name = "trip-rec-id", unique = true, def = "{'tripId': 1, 'driverId' : 1}")
public class TripEntity {

    @Id
    private String id;

    @Version
    private Integer version;

    private int driverId;
    private int tripId;
    private String startLocation;
    private String endLocation;
    private int rating;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TripEntity() {
    }

    @Override
    public String toString() {
        return format("TripEntity: %s/%d", driverId, tripId);
    }

    public String getId() {
        return id;
    }

    public Integer getVersion() {
        return version;
    }

    public int getDriverId() {
        return driverId;
    }

    public TripEntity setDriverId(int driverId) {
        this.driverId = driverId;
        return this;
    }

    public int getTripId() {
        return tripId;
    }

    public TripEntity setTripId(int tripId) {
        this.tripId = tripId;
        return this;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public TripEntity setStartLocation(String startLocation) {
        this.startLocation = startLocation;
        return this;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public TripEntity setEndLocation(String endLocation) {
        this.endLocation = endLocation;
        return this;
    }

    public int getRating() {
        return rating;
    }

    public TripEntity setRating(int rating) {
        this.rating = rating;
        return this;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public TripEntity setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public TripEntity setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }
}
