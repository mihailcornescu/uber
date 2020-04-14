package com.uber.api.core.driver;

public class Driver {
    private int driverId;
    private String name;
    private String phoneNo;
    private String serviceAddress;

    public Driver() {
        driverId = 0;
        name = null;
        phoneNo = null;
        serviceAddress = null;
    }

    public Driver(int driverId, String name, String phoneNo, String serviceAddress) {
        this.driverId = driverId;
        this.name = name;
        this.phoneNo = phoneNo;
        this.serviceAddress = serviceAddress;
    }

    public int getDriverId() {
        return driverId;
    }

    public String getName() {
        return name;
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
