package com.uber.api.core.driver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface DriverService {

    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/driver \
     *   -H "Content-Type: application/json" --data \
     *   '{"productId":123,"name":"product 123","phoneNo":123}'
     *
     * @param body
     * @return
     */
    @PostMapping(
            value    = "/driver",
            consumes = "application/json",
            produces = "application/json")
    Driver createDriver(@RequestBody Driver body);

    /**
     * Sample usage: curl $HOST:$PORT/driver/1
     *
     * @param driverId
     * @return the driver, if found, else null
     */
    @GetMapping(
        value    = "/driver/{driverId}",
        produces = "application/json")
     Driver getDriver(@PathVariable int driverId);

    void deleteDriver(@PathVariable int driverId);
}