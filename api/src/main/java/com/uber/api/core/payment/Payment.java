package com.uber.api.core.payment;

import java.time.LocalDateTime;

public class Payment {
    private int tripId;
    private int paymentId;
    private double price;
    private double reward;
    private LocalDateTime start;
    private LocalDateTime end;

    public Payment() {
        tripId = 0;
        paymentId = 0;
        price = 0;
        reward = 0;
        start = null;
        end = null;
    }

    public Payment(int tripId, int paymentId, double price, double reward, LocalDateTime start, LocalDateTime end) {
        this.tripId = tripId;
        this.paymentId = paymentId;
        this.price = price;
        this.reward = reward;
        this.start = start;
        this.end = end;
    }

    public int getTripId() {
        return tripId;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public double getPrice() {
        return price;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
}
