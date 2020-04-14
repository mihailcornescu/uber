package com.uber.microservices.core.history.services;

import com.uber.api.core.history.History;
import com.uber.api.core.history.HistoryService;
import com.uber.microservices.core.history.persistence.HistoryEntity;
import com.uber.microservices.core.history.persistence.HistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.uber.util.exceptions.InvalidInputException;
import com.uber.util.http.ServiceUtil;

@RestController
public class HistoryServiceImpl implements HistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(HistoryServiceImpl.class);

    private final HistoryRepository repository;

    private final HistoryMapper mapper;

    private final ServiceUtil serviceUtil;

    @Autowired
    public HistoryServiceImpl(HistoryRepository repository, HistoryMapper mapper, ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Mono<History> createDriverHistoryEntry(History body) {

        if (body.getDriverId() < 1) throw new InvalidInputException("Invalid driverId: " + body.getDriverId());

        HistoryEntity entity = mapper.apiToEntity(body);
        Mono<History> newEntity = repository.save(entity)
            .log()
            .onErrorMap(
                DuplicateKeyException.class,
                ex -> new InvalidInputException("Duplicate key, Driver Id: " + body.getDriverId() + ", History Id:" + body.getVehicleId()))
            .map(e -> mapper.entityToApi(e));

        return newEntity;
    }

    @Override
    public Flux<History> getDriverHistory(int driverId) {

        if (driverId < 1) throw new InvalidInputException("Invalid driverId: " + driverId);

        HistoryEntity founfEntity = repository.findByDriverId(driverId)
            .log()
            .map(e -> { mapper.entityToApi(e); return e;});
    }

    @Override
    public void deleteDriverHistory(int driverId) {

        if (driverId < 1) throw new InvalidInputException("Invalid driverId: " + driverId);

        LOG.debug("deleteDriverHistory: tries to delete deleteDriverHistory for the driver with deleteId: {}", driverId);
        repository.deleteAll(repository.findByDriverId(driverId)).block();
    }
}
