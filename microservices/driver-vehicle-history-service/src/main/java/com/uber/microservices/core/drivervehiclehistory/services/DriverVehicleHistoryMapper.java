package com.uber.microservices.core.drivervehiclehistory.services;

import com.uber.api.core.drivervehiclehistory.DriverVehicleHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import com.uber.microservices.core.drivervehiclehistory.persistence.DriverVehicleHistoryEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DriverVehicleHistoryMapper {

    DriverVehicleHistory entityToApi(DriverVehicleHistoryEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    DriverVehicleHistoryEntity apiToEntity(DriverVehicleHistory api);

    List<DriverVehicleHistory> entityListToApiList(List<DriverVehicleHistoryEntity> entity);
    List<DriverVehicleHistoryEntity> apiListToEntityList(List<DriverVehicleHistory> api);
}