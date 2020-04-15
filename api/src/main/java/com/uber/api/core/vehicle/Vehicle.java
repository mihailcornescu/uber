package com.uber.api.core.vehicle;

import java.time.LocalDateTime;

public class Vehicle {
    private int vehicleId;
    private String name;
    private String color;
    private String registrationNumber;
    private LocalDateTime registerTimestamp;

    public Vehicle() {
        vehicleId = 0;
        name = null;
        color = null;
    }

    public Vehicle(int vehicleId, String name, String color, String registrationNumber) {
        this.vehicleId = vehicleId;
        this.name = name;
        this.color = color;
        this.registrationNumber = registrationNumber;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public LocalDateTime getRegisterTimestamp() {
        return registerTimestamp;
    }
}
