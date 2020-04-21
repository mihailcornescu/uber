package com.uber.microservices.core.vehicle.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface VehicleRepository extends CrudRepository<VehicleEntity, String> {
    List<VehicleEntity> findByDriverId(int driverId);
}
