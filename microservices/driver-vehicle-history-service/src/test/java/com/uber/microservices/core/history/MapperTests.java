package com.uber.microservices.core.history;

import com.uber.api.core.history.History;
import com.uber.microservices.core.history.persistence.HistoryEntity;
import com.uber.microservices.core.history.services.HistoryMapper;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class MapperTests {

    private HistoryMapper mapper = Mappers.getMapper(HistoryMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        History api = new History(1, 2, LocalDate.now(), LocalDate.now());

        HistoryEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getDriverId(), entity.getDriverId());
        assertEquals(api.getVehicleId(), entity.getVehicleId());
        assertEquals(api.getDateFrom(), entity.getDateFrom());
        assertEquals(api.getDateTo(), entity.getDateTo());

        History api2 = mapper.entityToApi(entity);

        assertEquals(api.getDriverId(), api2.getDriverId());
        assertEquals(api.getVehicleId(), api2.getVehicleId());
        assertEquals(api.getDateFrom(), api2.getDateFrom());
        assertEquals(api.getDateTo(), api2.getDateTo());
    }

    @Test
    public void mapperListTests() {

        assertNotNull(mapper);

        History api = new History(1, 2, LocalDate.now(), LocalDate.now());
        List<History> apiList = Collections.singletonList(api);

        List<HistoryEntity> entityList = mapper.apiListToEntityList(apiList);
        assertEquals(apiList.size(), entityList.size());

        HistoryEntity entity = entityList.get(0);

        assertEquals(api.getDriverId(), entity.getDriverId());
        assertEquals(api.getVehicleId(), entity.getVehicleId());
        assertEquals(api.getDateFrom(), entity.getDateFrom());
        assertEquals(api.getDateTo(), entity.getDateTo());

        List<History> api2List = mapper.entityListToApiList(entityList);
        assertEquals(apiList.size(), api2List.size());

        History api2 = api2List.get(0);

        assertEquals(api.getDriverId(), api2.getDriverId());
        assertEquals(api.getVehicleId(), api2.getVehicleId());
        assertEquals(api.getDateFrom(), api2.getDateFrom());
        assertEquals(api.getDateTo(), api2.getDateTo());
    }
}
