package com.uber.api.core.trip;

import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface TripService {
    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/trip \
     *   -H "Content-Type: application/json" --data \
     *   '{"productId":123,"recommendationId":456,"author":"me","rate":5,"content":"yada, yada, yada"}'
     *
     * @param body
     * @return
     */
    @PostMapping(
            value    = "/trip",
            consumes = "application/json",
            produces = "application/json")
    Trip createTrip(@RequestBody Trip body);

    /**
     * Sample usage:
     *
     * curl $HOST:$PORT/trip?productId=1
     *
     * @param driverId
     * @return
     */
    @GetMapping(
            value    = "/trips",
            produces = "application/json")
    List<Trip> getTrips(@RequestParam(value = "driverId", required = true) int driverId);

    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/trip?productId=1
     *
     * @param driverId
     */
    @DeleteMapping(value = "/trip")
    void deleteTrips(@RequestParam(value = "driverId", required = true)  int driverId);
}
