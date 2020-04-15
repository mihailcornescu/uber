package com.uber.api.core.vehicle;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface VehicleService {

    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/vehicle \
     *   -H "Content-Type: application/json" --data \
     *   '{"vehicleId":123,"name":"color 123","registrationNumber":123}'
     *
     * @param body
     * @return
     */
    @PostMapping(
            value    = "/driver",
            consumes = "application/json",
            produces = "application/json")
    Vehicle createVehicle(@RequestBody Vehicle body);

    /**
     * Sample usage: curl $HOST:$PORT/vehicle/1
     *
     * @param vehicleId
     * @return the driver, if found, else null
     */
    @GetMapping(
        value    = "/vehicle/{vehicleId}",
        produces = "application/json")
    Vehicle getVehicle(@PathVariable int vehicleId);

    void deleteVehicle(@PathVariable int vehicleId);
}