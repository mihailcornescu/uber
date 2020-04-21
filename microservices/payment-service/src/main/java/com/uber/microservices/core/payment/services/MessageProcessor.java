package com.uber.microservices.core.payment.services;

import com.uber.api.core.payment.Payment;
import com.uber.api.core.payment.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import com.uber.api.event.Event;
import com.uber.util.exceptions.EventProcessingException;

@EnableBinding(Sink.class)
public class MessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final PaymentService paymentService;

    @Autowired
    public MessageProcessor(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Payment> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

        case CREATE:
            Payment payment = event.getData();
            LOG.info("Create payment with ID: {}/{}", payment.getTripId(), payment.getPaymentId());
            paymentService.createPayment(payment);
            break;

        case DELETE:
            int tripId = event.getKey();
            LOG.info("Delete payments with tripId: {}", tripId);
            paymentService.deletePayments(tripId);
            break;

        default:
            String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
            LOG.warn(errorMessage);
            throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
