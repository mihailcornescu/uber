package com.uber.microservices.core.vehicle;

import com.uber.api.core.vehicle.Vehicle;
import com.uber.microservices.core.vehicle.persistence.VehicleEntity;
import com.uber.microservices.core.vehicle.services.VehicleMapper;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.Assert.*;

public class MapperTests {

    private VehicleMapper mapper = Mappers.getMapper(VehicleMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        Vehicle api = new Vehicle(1, "n", "c", "r");

        VehicleEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getVehicleId(), entity.getVehicleId());
        assertEquals(api.getName(), entity.getName());
        assertEquals(api.getColor(), entity.getColor());
        assertEquals(api.getRegistrationNumber(), entity.getRegistrationNumber());
        assertEquals(api.getRegisterTimestamp(), entity.getRegisterTimestamp());

        Vehicle api2 = mapper.entityToApi(entity);

        assertEquals(api.getVehicleId(), api2.getVehicleId());
        assertEquals(api.getName(),      api2.getName());
        assertEquals(api.getColor(),      api2.getColor());
        assertEquals(api.getRegistrationNumber(), api2.getRegistrationNumber());
        assertEquals(api.getRegisterTimestamp(), api2.getRegisterTimestamp());
    }
}
