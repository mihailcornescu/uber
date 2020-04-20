package com.uber.microservices.core.trip.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static java.lang.String.format;

@Document(collection="trips")
public class TripEntity {

    @Id
    private String id;

    @Version
    private Integer version;

    @Indexed(unique = true)
    private int tripId;
    private String startLocation;
    private String endLocation;

    private int driverId;

    @Min(value = 1)
    @Max(value = 5)
    private int rate;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TripEntity() {
    }

    public TripEntity(int tripId, String startLocation, String endLocation) {
        this.tripId = tripId;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
    }

    @Override
    public String toString() {
        return format("TripEntity: %s", tripId);
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

    public int getDriverId() {
        return driverId;
    }

    public TripEntity setDriverId(int driverId) {
        this.driverId = driverId;
        return this;
    }

    public int getRate() {
        return rate;
    }

    public TripEntity setRate(int rate) {
        this.rate = rate;
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
