package com.uber.microservices.core.driver;

import com.uber.microservices.core.driver.persistence.DriverEntity;
import com.uber.microservices.core.driver.services.DriverMapper;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import com.uber.api.core.driver.Driver;

import static org.junit.Assert.*;

public class MapperTests {

    private DriverMapper mapper = Mappers.getMapper(DriverMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        Driver api = new Driver(1, "n", "pn");

        DriverEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getDriverId(), entity.getDriverId());
        assertEquals(api.getName(), entity.getName());
        assertEquals(api.getPhoneNo(), entity.getPhoneNo());

        Driver api2 = mapper.entityToApi(entity);

        assertEquals(api.getDriverId(), api2.getDriverId());
        assertEquals(api.getName(),      api2.getName());
        assertEquals(api.getPhoneNo(),      api2.getPhoneNo());
    }
}
