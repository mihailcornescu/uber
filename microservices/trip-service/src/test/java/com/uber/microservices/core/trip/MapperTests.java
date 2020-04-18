package com.uber.microservices.core.trip;

import com.uber.api.core.trip.Trip;
import com.uber.microservices.core.trip.persistence.TripEntity;
import com.uber.microservices.core.trip.services.TripMapper;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.Assert.*;

public class MapperTests {

    private TripMapper mapper = Mappers.getMapper(TripMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        Trip api = new Trip(1, "sl", "el");

        TripEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getTripId(), entity.getTripId());
        assertEquals(api.getStartLocation(), entity.getStartLocation());
        assertEquals(api.getEndLocation(), entity.getEndLocation());

        Trip api2 = mapper.entityToApi(entity);

        assertEquals(api.getTripId(), api2.getTripId());
        assertEquals(api.getStartLocation(),      api2.getStartLocation());
        assertEquals(api.getEndLocation(),      api2.getEndLocation());
    }
}
