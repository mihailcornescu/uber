package com.uber.microservices.core.driver.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface DriverRepository extends ReactiveCrudRepository<DriverEntity, String> {
    Mono<DriverEntity> findByDriverId(int driverId);
}
