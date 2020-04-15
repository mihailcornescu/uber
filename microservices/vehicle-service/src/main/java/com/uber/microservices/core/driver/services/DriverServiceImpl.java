package com.uber.microservices.core.driver.services;

import com.uber.microservices.core.driver.persistence.DriverEntity;
import com.uber.microservices.core.driver.persistence.DriverRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import com.uber.api.core.driver.Driver;
import com.uber.api.core.driver.DriverService;
import com.uber.util.exceptions.InvalidInputException;
import com.uber.util.exceptions.NotFoundException;
import com.uber.util.http.ServiceUtil;

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
        try {
            DriverEntity entity = mapper.apiToEntity(body);
            DriverEntity newEntity = repository.save(entity);

            LOG.debug("createProduct: entity created for driverId: {}", body.getDriverId());
            return mapper.entityToApi(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Driver Id: " + body.getDriverId());
        }    }

    @Override
    public Driver getDriver(int driverId) {

        if (driverId < 1) throw new InvalidInputException("Invalid driverId: " + driverId);

        DriverEntity entity = repository.findByDriverId(driverId)
                .orElseThrow(() -> new NotFoundException("No driver found for driverId: " + driverId));

        Driver response = mapper.entityToApi(entity);
        response.setServiceAddress(serviceUtil.getServiceAddress());

        LOG.debug("getDriver: found driverId: {}", response.getDriverId());

        return response;
    }

    @Override
    public void deleteDriver(int driverId) {
        LOG.debug("deleteProduct: tries to delete an entity with productId: {}", driverId);
        repository.findByDriverId(driverId).ifPresent(e -> repository.delete(e));
    }
}