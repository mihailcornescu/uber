package com.uber.microservices.core.trip.services;

import com.uber.api.core.trip.Trip;
import com.uber.microservices.core.trip.persistence.TripEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TripMapper {

    @Mappings({
            @Mapping(target = "driverId", ignore = true),
            @Mapping(target = "rate", ignore = true),
            @Mapping(target = "startTime", ignore = true),
            @Mapping(target = "endTime", ignore = true)
    })
    Trip entityToApi(TripEntity entity);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "version", ignore = true)
    })
    TripEntity apiToEntity(Trip api);

    List<Trip> entityListToApiList(List<TripEntity> entity);
    List<TripEntity> apiListToEntityList(List<Trip> api);
}
