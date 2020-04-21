package com.uber.api.composite.driver;

public class VehiclesSummary {

    private final int vehicleId;

    public VehiclesSummary() {
        this.vehicleId = 0;
    }

    public VehiclesSummary(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

}
