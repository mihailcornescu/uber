package com.uber.api.core.history;

import java.time.LocalDate;

public class History {
    private int driverId;
    private int vehicleId;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    public History() {
        driverId = 0;
        vehicleId = 0;
    }

    public History(int driverId, int vehicleId, LocalDate dateFrom, LocalDate dateTo) {
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public int getDriverId() {
        return driverId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }
}
