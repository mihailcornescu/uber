package com.uber.microservices.core.drivervehiclehistory.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

import static java.lang.String.format;

@Document(collection="driver-vehicle-history")
@CompoundIndex(name = "dv-hist-rec-id", unique = true, def = "{'driverId': 1, 'vehicleId' : 1}")
public class DriverVehicleHistoryEntity {

    @Id
    private String id;

    @Version
    private Integer version;

    private int driverId;
    private int vehicleId;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    public DriverVehicleHistoryEntity() {
    }

    public DriverVehicleHistoryEntity(int driverId, int vehicleId, LocalDate dateFrom, LocalDate dateTo) {
        this.driverId = driverId;
        this.vehicleId = vehicleId;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    @Override
    public String toString() {
        return format("DriverVehicleHistoryEntity: %s/%d", driverId, vehicleId);
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

    public int getVehicleId() {
        return vehicleId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVersion(Integer version) {
        this.version = version;
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
