package com.uber.api.core.vehicle;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Vehicle {
    private int driverId;
    private int vehicleId;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String name;
    private String color;
    private String registrationNumber;
    private LocalDateTime registerTimestamp;

    public Vehicle() {
        driverId = 0;
        vehicleId = 0;
        dateFrom = null;
        dateTo = null;
        name = null;
        color = null;
        registrationNumber = null;
        registerTimestamp = null;
    }

    public int getDriverId() {
        return driverId;
    }

    public Vehicle setDriverId(int driverId) {
        this.driverId = driverId;
        return this;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public Vehicle setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
        return this;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public Vehicle setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
        return this;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public Vehicle setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
        return this;
    }

    public String getName() {
        return name;
    }

    public Vehicle setName(String name) {
        this.name = name;
        return this;
    }

    public String getColor() {
        return color;
    }

    public Vehicle setColor(String color) {
        this.color = color;
        return this;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public Vehicle setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
        return this;
    }

    public LocalDateTime getRegisterTimestamp() {
        return registerTimestamp;
    }

    public Vehicle setRegisterTimestamp(LocalDateTime registerTimestamp) {
        this.registerTimestamp = registerTimestamp;
        return this;
    }
}
