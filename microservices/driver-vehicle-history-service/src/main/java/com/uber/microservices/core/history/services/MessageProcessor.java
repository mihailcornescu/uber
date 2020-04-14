package com.uber.microservices.core.history.services;

import com.uber.api.core.history.History;
import com.uber.api.core.history.HistoryService;
import com.uber.api.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

import com.uber.util.exceptions.EventProcessingException;

@EnableBinding(Sink.class)
public class MessageProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessor.class);

    private final HistoryService service;

    @Autowired
    public MessageProcessor(HistoryService service) {
        this.service = service;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, History> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

        case CREATE:
            History history = event.getData();
            LOG.info("Create history with ID: {}/{}", history.getDriverId(), history.getVehicleId());
            service.createDriverHistoryEntry(history);
            break;

        case DELETE:
            int driverId = event.getKey();
            LOG.info("Delete history with driverId: {}", driverId);
            service.deleteDriverHistory(driverId);
            break;

        default:
            String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
            LOG.warn(errorMessage);
            throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
