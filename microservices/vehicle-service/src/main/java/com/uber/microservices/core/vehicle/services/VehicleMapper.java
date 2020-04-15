package com.uber.microservices.core.vehicle.services;

import com.uber.api.core.vehicle.Vehicle;
import com.uber.microservices.core.vehicle.persistence.VehicleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    @Mappings({
            @Mapping(target = "registerTimestamp", ignore = true)
    })
    Vehicle entityToApi(VehicleEntity entity);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "version", ignore = true)
    })
    VehicleEntity apiToEntity(Vehicle api);
}
