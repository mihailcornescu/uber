package com.uber.microservices.core.trip.services;

import com.uber.api.core.trip.Trip;
import com.uber.microservices.core.trip.persistence.TripEntity;
import com.uber.microservices.core.trip.persistence.TripRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import com.uber.api.core.trip.TripService;
import com.uber.util.exceptions.InvalidInputException;
import com.uber.util.exceptions.NotFoundException;
import com.uber.util.http.ServiceUtil;

@RestController
public class TripServiceImpl implements TripService {

    private static final Logger LOG = LoggerFactory.getLogger(TripServiceImpl.class);

    private final ServiceUtil serviceUtil;

    private final TripRepository repository;

    private final TripMapper mapper;

    @Autowired
    public TripServiceImpl(TripRepository repository, TripMapper mapper, ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Trip createTrip(Trip body) {
        try {
            TripEntity entity = mapper.apiToEntity(body);
            TripEntity newEntity = repository.save(entity);

            LOG.debug("createTrip: entity created for tripId: {}", body.getTripId());
            return mapper.entityToApi(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Trip Id: " + body.getTripId());
        }    }

    @Override
    public Trip getTrip(int tripId) {

        if (tripId < 1) throw new InvalidInputException("Invalid tripId: " + tripId);

        TripEntity entity = repository.findByTripId(tripId)
                .orElseThrow(() -> new NotFoundException("No trip found for tripId: " + tripId));

        Trip response = mapper.entityToApi(entity);

        LOG.debug("getTrip: found tripId: {}", response.getTripId());

        return response;
    }

    @Override
    public void deleteTrip(int tripId) {
        LOG.debug("deleteTrip: tries to delete an entity with tripId: {}", tripId);
        repository.findByTripId(tripId).ifPresent(e -> repository.delete(e));
    }
}