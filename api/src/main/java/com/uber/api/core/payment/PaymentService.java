package com.uber.api.core.payment;

import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface PaymentService {

    /**
     * @param body
     * @return
     */
    @PostMapping(
            value    = "/payment",
            consumes = "application/json",
            produces = "application/json")
    Payment createPayment(@RequestBody Payment body);

    /**
     * Sample usage:
     *
     * curl $HOST:$PORT/payment?tripId=1
     *
     * @param tripId
     * @return
     */
    @GetMapping(
            value    = "/payment",
            produces = "application/json")
    Payment getPayment(@RequestParam(value = "tripId", required = true) int tripId);

    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/payment?tripId=1
     *
     * @param tripId
     */
    @DeleteMapping(value = "/payment")
    void deletePayments(@RequestParam(value = "tripId", required = true) int tripId);
}
