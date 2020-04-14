package com.uber.microservices.core.history.services;

import com.uber.api.core.history.History;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import com.uber.microservices.core.history.persistence.HistoryEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HistoryMapper {

    @Mappings({
        @Mapping(target = "dateFrom", source="entity.dateFrom"),
        @Mapping(target = "dateTo", source="entity.dateTo")
    })
    History entityToApi(HistoryEntity entity);

    @Mappings({
        @Mapping(target = "dateFrom", source="api.dateFrom"),
        @Mapping(target = "dateTo", source="api.dateTo"),
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "version", ignore = true)
    })
    HistoryEntity apiToEntity(History api);

    List<History> entityListToApiList(List<HistoryEntity> entity);
    List<HistoryEntity> apiListToEntityList(List<History> api);
}