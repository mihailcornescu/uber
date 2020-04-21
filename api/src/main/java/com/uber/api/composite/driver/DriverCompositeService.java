package com.uber.api.composite.driver;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.*;

@Api(description = "REST API for composite driver information.")
public interface DriverCompositeService {

    /**
     * Sample usage: curl $HOST:$PORT/driver-composite/1
     *
     * @param driverId
     * @return the composite driver info, if found, else null
     */
    @ApiOperation(
        value = "${api.driver-composite.get-composite-driver.description}",
        notes = "${api.driver-composite.get-composite-driver.notes}")
    @ApiResponses(value = {
        @ApiResponse(code = 400, message = "Bad Request, invalid format of the request. See response message for more information."),
        @ApiResponse(code = 404, message = "Not found, the specified id does not exist."),
        @ApiResponse(code = 422, message = "Unprocessable entity, input parameters caused the processing to fail. See response message for more information.")
    })
    @GetMapping(
        value    = "/driver-composite/{driverId}",
        produces = "application/json")
    DriverAggregate getCompositeDriver(@PathVariable int driverId);
}
