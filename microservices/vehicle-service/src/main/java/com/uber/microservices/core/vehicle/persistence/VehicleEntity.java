package com.uber.microservices.core.vehicle.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

import static java.lang.String.format;

@Document(collection="vehicles")
public class VehicleEntity {

    @Id
    private String id;

    @Version
    private Integer version;

    @Indexed(unique = true)
    private int vehicleId;

    private String name;
    private String color;
    private String registrationNumber;
    private LocalDateTime registerTimestamp;

    public VehicleEntity() {
    }

    public VehicleEntity(int vehicleId, String name, String color, String registrationNumber) {
        this.vehicleId = vehicleId;
        this.name = name;
        this.color = color;
        this.registrationNumber = registrationNumber;
        this.registerTimestamp = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return format("VehicleEntity: %s", vehicleId);
    }

    public String getId() {
        return id;
    }

    public Integer getVersion() {
        return version;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
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

