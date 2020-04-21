package com.uber.microservices.core.trip;

import com.uber.api.core.trip.Trip;
import com.uber.microservices.core.trip.persistence.TripEntity;
import com.uber.microservices.core.trip.services.RecommendationMapper;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class MapperTests {

    private RecommendationMapper mapper = Mappers.getMapper(RecommendationMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        Trip api = new Trip().setDriverId(1).setTripId(2).setStartLocation("sl1")
                .setEndLocation("el1");

        TripEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getDriverId(), entity.getDriverId());
        assertEquals(api.getTripId(), entity.getTripId());
        assertEquals(api.getStartLocation(), entity.getStartLocation());
        assertEquals(api.getEndLocation(), entity.getEndLocation());
        assertEquals(api.getRate(), entity.getRating());
        assertEquals(api.getStartTime(), entity.getStartTime());
        assertEquals(api.getEndTime(), entity.getEndTime());

        Trip api2 = mapper.entityToApi(entity);

        assertEquals(api.getDriverId(), api2.getDriverId());
        assertEquals(api.getTripId(), api2.getTripId());
        assertEquals(api.getStartLocation(), api2.getStartLocation());
        assertEquals(api.getEndLocation(), api2.getEndLocation());
        assertEquals(api.getRate(), api2.getRate());
        assertEquals(api.getStartTime(), api2.getStartTime());
        assertEquals(api.getEndTime(), api2.getEndTime());
    }

    @Test
    public void mapperListTests() {

        assertNotNull(mapper);

        Trip api = new Trip().setDriverId(1).setTripId(2).setStartLocation("sl1")
                .setEndLocation("el1");;
        List<Trip> apiList = Collections.singletonList(api);

        List<TripEntity> entityList = mapper.apiListToEntityList(apiList);
        assertEquals(apiList.size(), entityList.size());

        TripEntity entity = entityList.get(0);

        assertEquals(api.getDriverId(), entity.getDriverId());
        assertEquals(api.getTripId(), entity.getTripId());
        assertEquals(api.getStartLocation(), entity.getStartLocation());
        assertEquals(api.getEndLocation(), entity.getEndLocation());
        assertEquals(api.getRate(), entity.getRating());
        assertEquals(api.getStartTime(), entity.getStartTime());
        assertEquals(api.getEndTime(), entity.getEndTime());

        List<Trip> api2List = mapper.entityListToApiList(entityList);
        assertEquals(apiList.size(), api2List.size());

        Trip api2 = api2List.get(0);

        assertEquals(api.getDriverId(), api2.getDriverId());
        assertEquals(api.getTripId(), api2.getTripId());
        assertEquals(api.getStartLocation(), api2.getStartLocation());
        assertEquals(api.getEndLocation(), api2.getEndLocation());
        assertEquals(api.getRate(), api2.getRate());
        assertEquals(api.getStartTime(), api2.getStartTime());
        assertEquals(api.getEndTime(), api2.getEndTime());
    }
}
