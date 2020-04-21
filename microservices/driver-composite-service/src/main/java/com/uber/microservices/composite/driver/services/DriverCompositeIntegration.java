package com.uber.microservices.composite.driver.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uber.api.core.driver.Driver;
import com.uber.api.core.vehicle.Vehicle;
import com.uber.api.core.trip.Trip;
import com.uber.util.exceptions.InvalidInputException;
import com.uber.util.exceptions.NotFoundException;
import com.uber.util.http.HttpErrorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpMethod.GET;

@Component
public class DriverCompositeIntegration {

    private static final Logger LOG = LoggerFactory.getLogger(DriverCompositeIntegration.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;

    private final String driverServiceUrl;
    private final String driverVehicleHistoryServiceUrl;
    private final String tripServiceUrl;

    @Autowired
    public DriverCompositeIntegration(
        RestTemplate restTemplate,
        ObjectMapper mapper,

        @Value("${app.driver-service.host}") String driverServiceHost,
        @Value("${app.driver-service.port}") int    driverServicePort,

        @Value("${app.recommendation-service.host}") String recommendationServiceHost,
        @Value("${app.recommendation-service.port}") int    recommendationServicePort,

        @Value("${app.trip-service.host}") String tripServiceHost,
        @Value("${app.trip-service.port}") int    tripServicePort
    ) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;

        driverServiceUrl = "http://" + driverServiceHost + ":" + driverServicePort + "/driver";
        driverVehicleHistoryServiceUrl = "http://" + recommendationServiceHost + ":" + recommendationServicePort + "/recommendation";
        tripServiceUrl = "http://" + tripServiceHost + ":" + tripServicePort + "/trip";
    }

    public Driver getDriver(int driverId) {

        try {
            String url = driverServiceUrl + "/" + driverId;
            LOG.debug("Will call the getDriver API on URL: {}", url);

            Driver driver = restTemplate.getForObject(url, Driver.class);
            LOG.debug("Found a driver with id: {}", driver.getDriverId());

            return driver;

        } catch (HttpClientErrorException ex) {
            throw handleHttpClientException(ex);
        }
    }

    public List<Vehicle> getVechileHistory(int driverId) {

        try {
            String url = driverVehicleHistoryServiceUrl + "?driverId=" + driverId;

            LOG.debug("Will call the getRecommendations API on URL: {}", url);
            List<Vehicle> recommendations = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Vehicle>>() {}).getBody();

            LOG.debug("Found {} recommendations for a driver with id: {}", recommendations.size(), driverId);
            return recommendations;

        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting recommendations, return zero recommendations: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    public List<Trip> getTrips(int driverId) {

        try {
            String url = tripServiceUrl + "?driverId=" + driverId;

            LOG.debug("Will call the getRecommendations API on URL: {}", url);
            List<Trip> trips = restTemplate.exchange(url, GET, null, new ParameterizedTypeReference<List<Trip>>() {}).getBody();

            LOG.debug("Found {} trips for a driver with id: {}", trips.size(), driverId);
            return trips;

        } catch (Exception ex) {
            LOG.warn("Got an exception while requesting recommendations, return zero recommendations: {}", ex.getMessage());
            return new ArrayList<>();
        }
    }

    private RuntimeException handleHttpClientException(HttpClientErrorException ex) {
        switch (ex.getStatusCode()) {

        case NOT_FOUND:
            return new NotFoundException(getErrorMessage(ex));

        case UNPROCESSABLE_ENTITY :
            return new InvalidInputException(getErrorMessage(ex));

        default:
            LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
            LOG.warn("Error body: {}", ex.getResponseBodyAsString());
            return ex;
        }
    }

    private String getErrorMessage(HttpClientErrorException ex) {
        try {
            return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }
}