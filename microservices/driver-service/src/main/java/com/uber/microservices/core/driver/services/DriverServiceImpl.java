package com.uber.microservices.core.driver.services;

import com.uber.microservices.core.driver.persistence.DriverEntity;
import com.uber.microservices.core.driver.persistence.DriverRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import com.uber.api.core.driver.Driver;
import com.uber.api.core.driver.DriverService;
import com.uber.util.exceptions.InvalidInputException;
import com.uber.util.exceptions.NotFoundException;
import com.uber.util.http.ServiceUtil;

import static reactor.core.publisher.Mono.error;

@RestController
public class DriverServiceImpl implements DriverService {

    private static final Logger LOG = LoggerFactory.getLogger(DriverServiceImpl.class);

    private final ServiceUtil serviceUtil;

    private final DriverRepository repository;

    private final DriverMapper mapper;

    @Autowired
    public DriverServiceImpl(DriverRepository repository, DriverMapper mapper, ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Driver createDriver(Driver body) {

        if (body.getDriverId() < 1) throw new InvalidInputException("Invalid driverId: " + body.getDriverId());

        DriverEntity entity = mapper.apiToEntity(body);
        Mono<Driver> newEntity = repository.save(entity)
            .log()
            .onErrorMap(
                DuplicateKeyException.class,
                ex -> new InvalidInputException("Duplicate key, Driver Id: " + body.getDriverId()))
            .map(e -> mapper.entityToApi(e));

        return newEntity.block();
    }

    @Override
    public Mono<Driver> getDriver(int driverId) {

        if (driverId < 1) throw new InvalidInputException("Invalid driverId: " + driverId);

        return repository.findByDriverId(driverId)
            .switchIfEmpty(error(new NotFoundException("No driver found for driverId: " + driverId)))
            .log()
            .map(e -> mapper.entityToApi(e))
            .map(e -> {e.setServiceAddress(serviceUtil.getServiceAddress()); return e;});
    }

    @Override
    public void deleteDriver(int driverId) {

        if (driverId < 1) throw new InvalidInputException("Invalid driverId: " + driverId);

        LOG.debug("deleteDriver: tries to delete an entity with driverId: {}", driverId);
        repository.findByDriverId(driverId).log().map(e -> repository.delete(e)).flatMap(e -> e).block();
    }
}