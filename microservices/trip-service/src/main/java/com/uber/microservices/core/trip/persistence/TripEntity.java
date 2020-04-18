package com.uber.microservices.core.trip.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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

    public String getId() {
        return id;
    }

    public Integer getVersion() {
        return version;
    }

    public int getTripId() {
        return tripId;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVersion(Integer version) {
        this.version = version;
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
