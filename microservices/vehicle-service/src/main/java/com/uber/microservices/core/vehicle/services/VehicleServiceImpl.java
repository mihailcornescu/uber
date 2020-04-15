package com.uber.microservices.core.vehicle.services;

import com.uber.api.core.vehicle.Vehicle;
import com.uber.microservices.core.vehicle.persistence.VehicleEntity;
import com.uber.microservices.core.vehicle.persistence.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import com.uber.api.core.vehicle.VehicleService;
import com.uber.util.exceptions.InvalidInputException;
import com.uber.util.exceptions.NotFoundException;

@RestController
public class VehicleServiceImpl implements VehicleService {

    private static final Logger LOG = LoggerFactory.getLogger(VehicleServiceImpl.class);

    private final VehicleRepository repository;

    private final VehicleMapper mapper;

    @Autowired
    public VehicleServiceImpl(VehicleRepository repository, VehicleMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Vehicle createVehicle(Vehicle body) {
        try {
            VehicleEntity entity = mapper.apiToEntity(body);
            VehicleEntity newEntity = repository.save(entity);

            LOG.debug("deleteVehicle: entity created for vehicleId: {}", body.getVehicleId());
            return mapper.entityToApi(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Vehicle Id: " + body.getVehicleId());
        }    }

    @Override
    public Vehicle getVehicle(int vehicleId) {

        if (vehicleId < 1) throw new InvalidInputException("Invalid vehicleId: " + vehicleId);

        VehicleEntity entity = repository.findByVehicleId(vehicleId)
                .orElseThrow(() -> new NotFoundException("No vehicle found for vehicleId: " + vehicleId));

        Vehicle response = mapper.entityToApi(entity);

        LOG.debug("getVehicle: found vehicleId: {}", response.getVehicleId());

        return response;
    }

    @Override
    public void deleteVehicle(int vehicleId) {
        LOG.debug("deleteVehicle: tries to delete an entity with productId: {}", vehicleId);
        repository.findByVehicleId(vehicleId).ifPresent(e -> repository.delete(e));
    }
}