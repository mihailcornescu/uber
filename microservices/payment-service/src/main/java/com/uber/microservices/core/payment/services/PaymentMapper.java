package com.uber.microservices.core.payment.services;

import com.uber.microservices.core.payment.persistence.PaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import com.uber.api.core.payment.Payment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    Payment entityToApi(PaymentEntity entity);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "version", ignore = true)
    })
    PaymentEntity apiToEntity(Payment api);

    List<Payment> entityListToApiList(List<PaymentEntity> entity);
    List<PaymentEntity> apiListToEntityList(List<Payment> api);
}