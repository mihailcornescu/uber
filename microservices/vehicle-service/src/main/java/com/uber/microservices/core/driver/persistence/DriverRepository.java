package com.uber.microservices.core.driver.persistence;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface DriverRepository extends PagingAndSortingRepository<DriverEntity, String> {
    Optional<DriverEntity> findByDriverId(int driverId);
}
