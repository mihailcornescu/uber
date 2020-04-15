package com.uber.microservices.core.driver.services;

import com.uber.microservices.core.driver.persistence.DriverEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import com.uber.api.core.driver.Driver;

@Mapper(componentModel = "spring")
public interface DriverMapper {

    @Mappings({
            @Mapping(target = "serviceAddress", ignore = true)
    })
    Driver entityToApi(DriverEntity entity);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "version", ignore = true)
    })
    DriverEntity apiToEntity(Driver api);
}
