package com.uber.microservices.core.vehicle.services;

import com.uber.api.core.vehicle.VehicleService;
import com.uber.api.core.vehicle.Vehicle;
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

    private final VehicleService service;

    @Autowired
    public MessageProcessor(VehicleService service) {
        this.service = service;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Vehicle> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

        case CREATE:
            Vehicle vehicle = event.getData();
            LOG.info("Create vehicle with ID: {}", vehicle.getVehicleId());
            service.createVehicle(vehicle);
            break;

        case DELETE:
            int vehicleId = event.getKey();
            LOG.info("Delete history with vehicleId: {}", vehicleId);
            service.deleteVehicle(vehicleId);
            break;

        default:
            String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
            LOG.warn(errorMessage);
            throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
