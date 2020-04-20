package com.uber.microservices.composite.driver.services;

import com.uber.api.core.driver.Driver;
import com.uber.api.core.drivervehiclehistory.DriverVehicleHistory;
import com.uber.api.core.trip.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import com.uber.api.composite.driver.*;
import com.uber.util.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DriverCompositeServiceImpl implements DriverCompositeService {

    private static final Logger LOG = LoggerFactory.getLogger(DriverCompositeServiceImpl.class);

    private DriverCompositeIntegration integration;

    @Autowired
    public DriverCompositeServiceImpl(DriverCompositeIntegration integration) {
        this.integration = integration;
    }

    @Override
    public DriverAggregate getCompositeDriver(int driverId) {
        LOG.debug("getCompositeDriver: lookup a driver aggregate for driverId: {}", driverId);

        Driver driver = integration.getDriver(driverId);
        if (driver == null) throw new NotFoundException("No driver found for driverId: " + driverId);

        List<DriverVehicleHistory> vehicleHistory = integration.getVechileHistory(driverId);

        List<Trip> trips = integration.getTrips(driverId);

        LOG.debug("getCompositeDriver: aggregate entity found for driverId: {}", driverId);

        return createDriverAggregate(driver, vehicleHistory, trips);
    }

    private DriverAggregate createDriverAggregate(Driver driver, List<DriverVehicleHistory> vehicleHistory, List<Trip> trips) {

        // 1. Setup driver info
        int driverId = driver.getDriverId();
        String name = driver.getName();
        String weight = driver.getPhoneNo();

        // 2. Copy summary recommendation info, if available
        List<VehiclesSummary> vehicleSummaries = (vehicleHistory == null) ? null :
             vehicleHistory.stream()
                .map(r -> new VehiclesSummary(r.getVehicleId()))
                .collect(Collectors.toList());

        // 2. Copy summary trip info, if available
        List<TripsSummary> tripsSummaries = (trips == null) ? null :
                trips.stream()
                        .map(r -> new TripsSummary().setTripId(1))
                        .collect(Collectors.toList());
        return new DriverAggregate(driverId, name, weight, vehicleSummaries, tripsSummaries);
    }
}