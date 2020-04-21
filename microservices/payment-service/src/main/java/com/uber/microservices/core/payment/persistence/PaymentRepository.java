package com.uber.microservices.core.payment.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PaymentRepository extends CrudRepository<PaymentEntity, String> {
    PaymentEntity findByPaymentId(int paymentId);
}
