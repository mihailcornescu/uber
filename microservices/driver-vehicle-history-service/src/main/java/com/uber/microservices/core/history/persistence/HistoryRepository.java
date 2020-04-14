package com.uber.microservices.core.history.persistence;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface HistoryRepository extends ReactiveCrudRepository<HistoryEntity, String> {
    Flux<HistoryEntity> findByDriverId(int driverId);
}
