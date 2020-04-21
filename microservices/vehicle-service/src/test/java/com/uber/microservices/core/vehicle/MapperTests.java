package com.uber.microservices.core.vehicle;

import com.uber.api.core.vehicle.Vehicle;
import com.uber.microservices.core.vehicle.persistence.VehicleEntity;
import com.uber.microservices.core.vehicle.services.VehicleMapper;
import com.uber.util.Constants;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class MapperTests {

    private VehicleMapper mapper = Mappers.getMapper(VehicleMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        Vehicle api = new Vehicle().setDriverId(1).setVehicleId(2).setDateFrom(LocalDate.of(2018, 01, 01))
            .setDateTo(Constants.END_OF_DATE);

        VehicleEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getDriverId(), entity.getDriverId());
        assertEquals(api.getVehicleId(), entity.getVehicleId());
        assertEquals(api.getDateFrom(), entity.getDateFrom());
        assertEquals(api.getDateTo(), entity.getDateTo());

        Vehicle api2 = mapper.entityToApi(entity);


        assertEquals(api.getDriverId(), api2.getDriverId());
        assertEquals(api.getVehicleId(), api2.getVehicleId());
        assertEquals(api.getDateFrom(), api2.getDateFrom());
        assertEquals(api.getDateTo(), api2.getDateTo());
    }

    @Test
    public void mapperListTests() {

        assertNotNull(mapper);


        Vehicle api = new Vehicle().setDriverId(1).setVehicleId(2).setDateFrom(LocalDate.of(2018, 01, 01))
                .setDateTo(Constants.END_OF_DATE);

        List<Vehicle> apiList = Collections.singletonList(api);

        List<VehicleEntity> entityList = mapper.apiListToEntityList(apiList);
        assertEquals(apiList.size(), entityList.size());

        VehicleEntity entity = entityList.get(0);

        assertEquals(api.getDriverId(), entity.getDriverId());
        assertEquals(api.getVehicleId(), entity.getVehicleId());
        assertEquals(api.getDateFrom(), entity.getDateFrom());
        assertEquals(api.getDateTo(), entity.getDateTo());


        List<Vehicle> api2List = mapper.entityListToApiList(entityList);
        assertEquals(apiList.size(), api2List.size());

        Vehicle api2 = api2List.get(0);

        assertEquals(api.getDriverId(), api2.getDriverId());
        assertEquals(api.getVehicleId(), api2.getVehicleId());
        assertEquals(api.getDateFrom(), api2.getDateFrom());
        assertEquals(api.getDateTo(), api2.getDateTo());
    }
}
