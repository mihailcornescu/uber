package com.uber.api.composite.driver;

import java.util.List;

public class DriverAggregate {
    private final int driverId;
    private final String name;
    private final String phoneNo;
    private final List<VehiclesSummary> vehicles;
    private final List<TripsSummary> trips;

    public DriverAggregate() {
        driverId = 0;
        name = null;
        phoneNo = null;
        vehicles = null;
        trips = null;
    }

    public DriverAggregate(
            int driverId,
            String name,
            String phoneNo,
            List<VehiclesSummary> vehicles,
            List<TripsSummary> trips) {

        this.driverId = driverId;
        this.name = name;
        this.phoneNo = phoneNo;
        this.vehicles = vehicles;
        this.trips = trips;
    }

    public int getDriverId() {
        return driverId;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public List<VehiclesSummary> getVehicles() {
        return vehicles;
    }

    public List<TripsSummary> getTrips() {
        return trips;
    }
}
