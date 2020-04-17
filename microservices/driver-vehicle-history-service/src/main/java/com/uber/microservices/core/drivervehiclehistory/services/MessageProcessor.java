package com.uber.microservices.core.drivervehiclehistory.services;

import com.uber.api.core.drivervehiclehistory.DriverVehicleHistory;
import com.uber.api.core.drivervehiclehistory.DriverVehicleHistoryService;
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

    private final DriverVehicleHistoryService driverVehicleHistoryService;

    @Autowired
    public MessageProcessor(DriverVehicleHistoryService driverVehicleHistoryService) {
        this.driverVehicleHistoryService = driverVehicleHistoryService;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, DriverVehicleHistory> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

        case CREATE:
            DriverVehicleHistory history = event.getData();
            LOG.info("Create history with ID: {}/{}", history.getDriverId(), history.getVehicleId());
            driverVehicleHistoryService.createDriverVehicleHistory(history);
            break;

        case DELETE:
            int driverId = event.getKey();
            LOG.info("Delete driver's vehicle history for driver with DriverID: {}", driverId);
            driverVehicleHistoryService.deleteDriverVehicleHistory(driverId);
            break;

        default:
            String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
            LOG.warn(errorMessage);
            throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
