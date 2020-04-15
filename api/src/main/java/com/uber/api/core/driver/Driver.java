package com.uber.api.core.driver;

public class Driver {
    private int driverId;
    private String name;
    private String phoneNo;

    public Driver() {
        driverId = 0;
        name = null;
        phoneNo = null;
    }

    public Driver(int driverId, String name, String phoneNo) {
        this.driverId = driverId;
        this.name = name;
        this.phoneNo = phoneNo;
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

}
