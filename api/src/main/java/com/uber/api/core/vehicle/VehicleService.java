package com.uber.api.core.vehicle;

import org.springframework.web.bind.annotation.*;

import java.util.List;

public interface VehicleService {

    /**
     * Sample usage:
     *
     * curl -X POST $HOST:$PORT/driver-vehicle-history \
     *   -H "Content-Type: application/json" --data \
     *   '{"driverId":123,"vehicleId":456,"author":"me","rate":5,"content":"yada, yada, yada"}'
     *
     * @param body
     * @return
     */
    @PostMapping(
            value    = "/driver-vehicle-history",
            consumes = "application/json",
            produces = "application/json")
    Vehicle createDriverVehicleHistory(@RequestBody Vehicle body);

    /**
     * Sample usage:
     *
     * curl $HOST:$PORT/driver-vehicle-history?driverId=1
     *
     * @param driverId
     * @return
     */
    @GetMapping(
            value    = "/driver-vehicle-history",
            produces = "application/json")
    List<Vehicle> getDriversVehicleHistory(@RequestParam(value = "driverId", required = true) int driverId);

    /**
     * Sample usage:
     *
     * curl -X DELETE $HOST:$PORT/driver-vehicle-history?driverId=1
     *
     * @param driverId
     */
    @DeleteMapping(value = "/driver-vehicle-history")
    void deleteDriverVehicleHistory(@RequestParam(value = "driverId", required = true)  int driverId);
}
