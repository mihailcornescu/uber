package com.uber.microservices.core.vehicle.persistence;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface VehicleRepository extends PagingAndSortingRepository<VehicleEntity, String> {
    Optional<VehicleEntity> findByVehicleId(int vehicleId);
}
