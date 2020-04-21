package com.uber.microservices.core.trip.services;

import com.uber.api.core.trip.Trip;
import com.uber.microservices.core.trip.persistence.TripEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    @Mappings({
            @Mapping(target = "rate", source="entity.rating"),
    })
    Trip entityToApi(TripEntity entity);

    @Mappings({
            @Mapping(target = "rating", source="api.rate"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    TripEntity apiToEntity(Trip api);

    List<Trip> entityListToApiList(List<TripEntity> entity);
    List<TripEntity> apiListToEntityList(List<Trip> api);
}