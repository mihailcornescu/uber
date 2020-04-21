package com.uber.microservices.core.payment.persistence;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

import static java.lang.String.format;

@Document(collection="payments")
@CompoundIndex(name = "pay-rec-id", unique = true, def = "{'tripId': 1, 'paymentId' : 1}")
public class PaymentEntity {

    @Id
    private String id;

    @Version
    private Integer version;

    private int tripId;
    private int paymentId;
    private double price;
    private double reward;
    private LocalDateTime start;
    private LocalDateTime end;

    public PaymentEntity() {
    }

    @Override
    public String toString() {
        return format("PaymentEntity: %s/%d", tripId, paymentId);
    }

    public String getId() {
        return id;
    }

    public Integer getVersion() {
        return version;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getReward() {
        return reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
}
