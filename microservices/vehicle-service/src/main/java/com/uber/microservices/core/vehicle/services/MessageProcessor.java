package com.uber.microservices.core.vehicle.services;

import com.uber.api.core.vehicle.Vehicle;
import com.uber.api.core.vehicle.VehicleService;
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

    private final VehicleService vehicleService;

    @Autowired
    public MessageProcessor(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Vehicle> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

        case CREATE:
            Vehicle history = event.getData();
            LOG.info("Create history with ID: {}/{}", history.getDriverId(), history.getVehicleId());
            vehicleService.createDriverVehicleHistory(history);
            break;

        case DELETE:
            int driverId = event.getKey();
            LOG.info("Delete driver's vehicle history for driver with DriverID: {}", driverId);
            vehicleService.deleteDriverVehicleHistory(driverId);
            break;

        default:
            String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
            LOG.warn(errorMessage);
            throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
