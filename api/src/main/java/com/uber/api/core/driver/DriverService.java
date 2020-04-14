package com.uber.api.core.driver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

public interface DriverService {

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
     Mono<Driver> getDriver(@PathVariable int driverId);

    void deleteDriver(@PathVariable int driverId);
}