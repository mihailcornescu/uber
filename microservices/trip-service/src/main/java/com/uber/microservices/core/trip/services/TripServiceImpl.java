package com.uber.microservices.core.trip.services;

import com.uber.api.core.trip.Trip;
import com.uber.microservices.core.trip.persistence.TripEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import com.uber.api.core.trip.TripService;
import com.uber.microservices.core.trip.persistence.TripRepository;
import com.uber.util.exceptions.InvalidInputException;
import com.uber.util.http.ServiceUtil;

import java.util.List;

@RestController
public class TripServiceImpl implements TripService {

    private static final Logger LOG = LoggerFactory.getLogger(TripServiceImpl.class);

    private final TripRepository repository;

    private final RecommendationMapper mapper;

    private final ServiceUtil serviceUtil;

    @Autowired
    public TripServiceImpl(TripRepository repository, RecommendationMapper mapper, ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Trip createTrip(Trip body) {
        try {
            TripEntity entity = mapper.apiToEntity(body);
            TripEntity newEntity = repository.save(entity);

            LOG.debug("createRecommendation: created a recommendation entity: {}/{}", body.getDriverId(), body.getTripId());
            return mapper.entityToApi(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Driver Id: " + body.getDriverId() + ", Trip Id:" + body.getTripId());
        }
    }

    @Override
    public List<Trip> getTrips(int driverId) {

        if (driverId < 1) throw new InvalidInputException("Invalid driverId: " + driverId);

        List<TripEntity> entity = repository.findByDriverId(driverId);
        return mapper.entityListToApiList(entity);
    }

    @Override
    public void deleteTrips(int driverId) {
        LOG.debug("deleteTrips: tries to delete trips for driver with driverId: {}", driverId);
        repository.deleteAll(repository.findByDriverId(driverId));
    }
}
