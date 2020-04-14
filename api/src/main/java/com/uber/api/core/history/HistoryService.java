package com.uber.api.core.history;

import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HistoryService {

    /**
     * Sample usage:
     *
     * curl $HOST:$PORT/driver-history?driverId=1
     *
     * @param driverId
     * @return
     */
    @PostMapping(
            value    = "/driver-history",
            produces = "application/json")
    Mono<History> createDriverHistoryEntry(@RequestBody History body);

    /**
     * Sample usage:
     *
     * curl $HOST:$PORT/driver-history?driverId=1
     *
     * @param driverId
     * @return
     */
    @GetMapping(
        value    = "/driver-history",
        produces = "application/json")
    Flux<History> getDriverHistory(@RequestParam(value = "driverId", required = true) int driverId);

    void deleteDriverHistory(@RequestParam(value = "driverId", required = true)  int driverId);
}
