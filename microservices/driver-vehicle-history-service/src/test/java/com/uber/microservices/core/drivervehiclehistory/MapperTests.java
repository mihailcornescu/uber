package com.uber.microservices.core.drivervehiclehistory;

import com.uber.microservices.core.drivervehiclehistory.persistence.DriverVehicleHistoryEntity;
import com.uber.microservices.core.drivervehiclehistory.services.DriverVehicleHistoryMapper;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import com.uber.api.core.drivervehiclehistory.DriverVehicleHistory;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class MapperTests {

    private DriverVehicleHistoryMapper mapper = Mappers.getMapper(DriverVehicleHistoryMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        DriverVehicleHistory api = new DriverVehicleHistory(1, 2, LocalDate.of(2018, 01, 01), LocalDate.now());

        DriverVehicleHistoryEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getDriverId(), entity.getDriverId());
        assertEquals(api.getVehicleId(), entity.getVehicleId());
        assertEquals(api.getDateFrom(), entity.getDateFrom());
        assertEquals(api.getDateTo(), entity.getDateTo());

        DriverVehicleHistory api2 = mapper.entityToApi(entity);


        assertEquals(api.getDriverId(), api2.getDriverId());
        assertEquals(api.getVehicleId(), api2.getVehicleId());
        assertEquals(api.getDateFrom(), api2.getDateFrom());
        assertEquals(api.getDateTo(), api2.getDateTo());
    }

    @Test
    public void mapperListTests() {

        assertNotNull(mapper);

        DriverVehicleHistory api = new DriverVehicleHistory(1, 2, LocalDate.of(2018, 01, 01), LocalDate.now());
        List<DriverVehicleHistory> apiList = Collections.singletonList(api);

        List<DriverVehicleHistoryEntity> entityList = mapper.apiListToEntityList(apiList);
        assertEquals(apiList.size(), entityList.size());

        DriverVehicleHistoryEntity entity = entityList.get(0);

        assertEquals(api.getDriverId(), entity.getDriverId());
        assertEquals(api.getVehicleId(), entity.getVehicleId());
        assertEquals(api.getDateFrom(), entity.getDateFrom());
        assertEquals(api.getDateTo(), entity.getDateTo());


        List<DriverVehicleHistory> api2List = mapper.entityListToApiList(entityList);
        assertEquals(apiList.size(), api2List.size());

        DriverVehicleHistory api2 = api2List.get(0);

        assertEquals(api.getDriverId(), api2.getDriverId());
        assertEquals(api.getVehicleId(), api2.getVehicleId());
        assertEquals(api.getDateFrom(), api2.getDateFrom());
        assertEquals(api.getDateTo(), api2.getDateTo());
    }
}
