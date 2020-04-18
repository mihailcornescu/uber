package com.uber.api.core.trip;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface TripService {

    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/trip \
     *   -H "Content-Type: application/json" --data \
     *   '{"productId":123,"name":"product 123","phoneNo":123}'
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
     * Sample usage: curl $HOST:$PORT/trip/1
     *
     * @param tripId
     * @return the trip, if found, else null
     */
    @GetMapping(
        value    = "/trip/{tripId}",
        produces = "application/json")
    Trip getTrip(@PathVariable int tripId);

    void deleteTrip(@PathVariable int tripId);
}