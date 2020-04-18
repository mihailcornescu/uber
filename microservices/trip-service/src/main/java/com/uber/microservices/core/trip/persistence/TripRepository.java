package com.uber.microservices.core.trip.persistence;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface TripRepository extends PagingAndSortingRepository<TripEntity, String> {
    Optional<TripEntity> findByTripId(int tripId);
}
