package com.uber.api.core.drivervehiclehistory;

import java.time.LocalDate;

public class DriverVehicleHistory {
    private int driverId;
    private int vehicleId;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    public DriverVehicleHistory() {
        driverId = 0;
        vehicleId = 0;
        dateFrom = null;
        dateTo = null;
    }

    public DriverVehicleHistory(int driverId, int vehicleId, LocalDate dateFrom, LocalDate dateTo) {
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
