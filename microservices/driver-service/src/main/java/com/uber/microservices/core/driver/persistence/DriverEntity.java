package com.uber.microservices.core.driver.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import static java.lang.String.format;

@Document(collection="drivers")
public class DriverEntity {

    @Id
    private String id;

    @Version
    private Integer version;

    @Indexed(unique = true)
    private int driverId;

    private String name;
    private String phoneNo;
    private String serviceAddress;

    public DriverEntity() {
    }

    public DriverEntity(int driverId, String name, String phoneNo, String serviceAddress) {
        this.driverId = driverId;
        this.name = name;
        this.phoneNo = phoneNo;
        this.serviceAddress = serviceAddress;
    }

    @Override
    public String toString() {
        return format("DriverEntity: %s", driverId);
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

    public String getName() {
        return name;
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

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }
}
