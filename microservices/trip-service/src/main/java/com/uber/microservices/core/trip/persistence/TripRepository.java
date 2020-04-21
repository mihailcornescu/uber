package com.uber.microservices.core.trip.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TripRepository extends CrudRepository<TripEntity, String> {
    List<TripEntity> findByDriverId(int driverId);
    TripEntity findByTripId(int tripId);
}
