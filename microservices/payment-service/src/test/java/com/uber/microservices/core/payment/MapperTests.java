package com.uber.microservices.core.payment;

import com.uber.api.core.payment.Payment;
import com.uber.microservices.core.payment.persistence.PaymentEntity;
import com.uber.microservices.core.payment.services.PaymentMapper;
import com.uber.util.Constants;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class MapperTests {

    private PaymentMapper mapper = Mappers.getMapper(PaymentMapper.class);

    @Test
    public void mapperTests() {

        assertNotNull(mapper);

        Payment api = new Payment(1, 2, 12.5, 12.5, LocalDateTime.of(2020, 01,05, 12, 44), Constants.END_OF_TIME);

        PaymentEntity entity = mapper.apiToEntity(api);

        assertEquals(api.getTripId(), entity.getTripId());
        assertEquals(api.getPaymentId(), entity.getPaymentId());
        assertEquals(api.getPrice(), entity.getPrice(), 2);
        assertEquals(api.getStart(), entity.getStart());

        Payment api2 = mapper.entityToApi(entity);

        assertEquals(api.getTripId(), api2.getTripId());
        assertEquals(api.getPaymentId(), api2.getPaymentId());
        assertEquals(api.getPrice(), api2.getPrice(), 2);
        assertEquals(api.getStart(), api2.getStart());
    }

    @Test
    public void mapperListTests() {

        assertNotNull(mapper);

        Payment api = new Payment(1, 2, 12.5, 12.5, LocalDateTime.of(2020, 01,05, 12, 44), Constants.END_OF_TIME);        List<Payment> apiList = Collections.singletonList(api);

        List<PaymentEntity> entityList = mapper.apiListToEntityList(apiList);
        assertEquals(apiList.size(), entityList.size());

        PaymentEntity entity = entityList.get(0);

        assertEquals(api.getTripId(), entity.getTripId());
        assertEquals(api.getPaymentId(), entity.getPaymentId());
        assertEquals(api.getPrice(), entity.getPrice(), 2);
        assertEquals(api.getStart(), entity.getStart());

        List<Payment> api2List = mapper.entityListToApiList(entityList);
        assertEquals(apiList.size(), api2List.size());

        Payment api2 = api2List.get(0);

        assertEquals(api.getTripId(), api2.getTripId());
        assertEquals(api.getPaymentId(), api2.getPaymentId());
        assertEquals(api.getPrice(), api2.getPrice(), 2);
        assertEquals(api.getStart(), api2.getStart());
    }
}
