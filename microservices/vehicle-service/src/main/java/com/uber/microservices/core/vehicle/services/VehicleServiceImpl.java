package com.uber.microservices.core.vehicle.services;

import com.uber.api.core.vehicle.Vehicle;
import com.uber.api.core.vehicle.VehicleService;
import com.uber.microservices.core.vehicle.persistence.VehicleEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import com.uber.microservices.core.vehicle.persistence.VehicleRepository;
import com.uber.util.exceptions.InvalidInputException;
import com.uber.util.http.ServiceUtil;

import java.util.List;

@RestController
public class VehicleServiceImpl implements VehicleService {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleServiceImpl.class);

    private final VehicleRepository repository;

    private final VehicleMapper mapper;

    private final ServiceUtil serviceUtil;

    @Autowired
    public VehicleServiceImpl(VehicleRepository repository, VehicleMapper mapper, ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Vehicle createDriverVehicleHistory(Vehicle body) {
        try {
            VehicleEntity entity = mapper.apiToEntity(body);
            VehicleEntity newEntity = repository.save(entity);

            LOG.debug("createDriverVehicleHistory: created a driver's vehicle history entity: {}/{}", body.getDriverId(), body.getVehicleId());
            return mapper.entityToApi(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Driver Id: " + body.getDriverId() + ", Vehicle Id:" + body.getVehicleId());
        }
    }

    @Override
    public List<Vehicle> getDriversVehicleHistory(int driverId) {

        if (driverId < 1) throw new InvalidInputException("Invalid driverId: " + driverId);

        List<VehicleEntity> entityList = repository.findByDriverId(driverId);
        List<Vehicle> list = mapper.entityListToApiList(entityList);

        LOG.debug("getDriversVehicleHistory: response size: {}", list.size());

        return list;
    }

    @Override
    public void deleteDriverVehicleHistory(int driverId) {
        LOG.debug("deleteDriverVehicleHistory: tries to delete drivers vehicle history for the driver with driverId: {}", driverId);
        repository.deleteAll(repository.findByDriverId(driverId));
    }
}
