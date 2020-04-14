package com.uber.microservices.core.driver;

import com.uber.microservices.core.driver.persistence.DriverEntity;
import com.uber.microservices.core.driver.services.DriverMapper;
import org.junit.Test;
import org.mapstruct.factory.Mappers;
import com.uber.api.core.product.Product;

import static org.junit.Assert.*;

public class MapperTests {

    private DriverMapper mapper = Mappers.getMapper(DriverMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        Product api = new Product(1, "n", 1, "sa");

        DriverEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getProductId(), entity.getDriverId());
        assertEquals(api.getProductId(), entity.getDriverId());
        assertEquals(api.getName(), entity.getName());

        Product api2 = mapper.entityToApi(entity);

        assertEquals(api.getProductId(), api2.getProductId());
        assertEquals(api.getProductId(), api2.getProductId());
        assertEquals(api.getName(),      api2.getName());
        assertNull(api2.getServiceAddress());
    }
}
