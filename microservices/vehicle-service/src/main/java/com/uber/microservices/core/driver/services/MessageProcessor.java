package com.uber.microservices.core.driver.services;

import com.uber.api.core.driver.DriverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import com.uber.api.core.driver.Driver;
import com.uber.api.core.driver.DriverService;
import com.uber.api.event.Event;
import com.uber.util.exceptions.EventProcessingException;

@EnableBinding(Sink.class)
public class MessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final DriverService service;

    @Autowired
    public MessageProcessor(DriverService service) {
        this.service = service;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Driver> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

        case CREATE:
            Driver driver = event.getData();
            LOG.info("Create driver with ID: {}", driver.getDriverId());
            service.createDriver(driver);
            break;

        case DELETE:
            int driverId = event.getKey();
            LOG.info("Delete history with driverID: {}", driverId);
            service.deleteDriver(driverId);
            break;

        default:
            String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
            LOG.warn(errorMessage);
            throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
