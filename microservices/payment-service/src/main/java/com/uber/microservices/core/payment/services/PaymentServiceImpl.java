package com.uber.microservices.core.payment.services;

import com.uber.api.core.payment.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import com.uber.api.core.payment.PaymentService;
import com.uber.microservices.core.payment.persistence.PaymentEntity;
import com.uber.microservices.core.payment.persistence.PaymentRepository;
import com.uber.util.exceptions.InvalidInputException;
import com.uber.util.http.ServiceUtil;

@RestController
public class PaymentServiceImpl implements PaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository repository;

    private final PaymentMapper mapper;

    private final ServiceUtil serviceUtil;

    @Autowired
    public PaymentServiceImpl(PaymentRepository repository, PaymentMapper mapper, ServiceUtil serviceUtil) {
        this.repository = repository;
        this.mapper = mapper;
        this.serviceUtil = serviceUtil;
    }

    @Override
    public Payment createPayment(Payment body) {
        try {
            PaymentEntity entity = mapper.apiToEntity(body);
            PaymentEntity newEntity = repository.save(entity);

            LOG.debug("createRecommendation: created a recommendation entity: {}/{}", body.getTripId(), body.getPaymentId());
            return mapper.entityToApi(newEntity);

        } catch (DuplicateKeyException dke) {
            throw new InvalidInputException("Duplicate key, Product Id: " + body.getTripId() + ", Payment Id:" + body.getPaymentId());
        }
    }

    @Override
    public Payment getPayment(int tripId) {

        if (tripId < 1) throw new InvalidInputException("Invalid tripId: " + tripId);

        PaymentEntity entityList = repository.findByPaymentId(tripId);
        return mapper.entityToApi(entityList);
    }

    @Override
    public void deletePayments(int tripId) {
        LOG.debug("deletePayments: tries to delete payments for the product with tripId: {}", tripId);
        repository.delete(repository.findByPaymentId(tripId));
    }
}
