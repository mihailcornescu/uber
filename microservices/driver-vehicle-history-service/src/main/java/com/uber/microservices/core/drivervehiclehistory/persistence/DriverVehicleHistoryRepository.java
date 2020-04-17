package com.uber.microservices.core.drivervehiclehistory.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DriverVehicleHistoryRepository extends CrudRepository<DriverVehicleHistoryEntity, String> {
    List<DriverVehicleHistoryEntity> findByDriverId(int driverId);
}
