package com.uber.microservices.core.drivervehiclehistory.services;

import com.uber.api.core.drivervehiclehistory.DriverVehicleHistory;
import com.uber.api.core.drivervehiclehistory.DriverVehicleHistoryService;
import com.uber.microservices.core.drivervehiclehistory.persistence.DriverVehicleHistoryEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import com.uber.microservices.core.drivervehiclehistory.persistence.DriverVehicleHistoryRepository;
import com.uber.util.exceptions.InvalidInputException;
import com.uber.util.http.ServiceUtil;

import java.util.List;

@RestController
public class DriverVehicleHistoryServiceImpl implements DriverVehicleHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(DriverVehicleHistoryServiceImpl.class);

    private final DriverVehicleHistoryRepository repository;

    private final DriverVehicleHistoryMapper mapper;

    private final ServiceUtil serviceUtil;

    @Autowired
    public DriverVehicleHistoryServiceImpl(DriverVehicleHistoryRepository repository, DriverVehicleHistoryMapper mapper, ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public DriverVehicleHistory createDriverVehicleHistory(DriverVehicleHistory body) {
        try {
            DriverVehicleHistoryEntity entity = mapper.apiToEntity(body);
            DriverVehicleHistoryEntity newEntity = repository.save(entity);

            LOG.debug("createDriverVehicleHistory: created a driver's vehicle history entity: {}/{}", body.getDriverId(), body.getVehicleId());
            return mapper.entityToApi(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Driver Id: " + body.getDriverId() + ", Vehicle Id:" + body.getVehicleId());
        }
    }

    @Override
    public List<DriverVehicleHistory> getDriversVehicleHistory(int driverId) {

        if (driverId < 1) throw new InvalidInputException("Invalid driverId: " + driverId);

        List<DriverVehicleHistoryEntity> entityList = repository.findByDriverId(driverId);
        List<DriverVehicleHistory> list = mapper.entityListToApiList(entityList);

        LOG.debug("getDriversVehicleHistory: response size: {}", list.size());

        return list;
    }

    @Override
    public void deleteDriverVehicleHistory(int driverId) {
        LOG.debug("deleteDriverVehicleHistory: tries to delete drivers vehicle history for the driver with driverId: {}", driverId);
        repository.deleteAll(repository.findByDriverId(driverId));
    }
}
