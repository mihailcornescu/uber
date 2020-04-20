package com.uber.microservices.core.trip.services;

import com.uber.api.core.trip.TripService;
import com.uber.api.core.trip.Trip;
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

    private final TripService service;

    @Autowired
    public MessageProcessor(TripService service) {
        this.service = service;
    }

    @StreamListener(target = Sink.INPUT)
    public void process(Event<Integer, Trip> event) {

        LOG.info("Process message created at {}...", event.getEventCreatedAt());

        switch (event.getEventType()) {

        case CREATE:
            Trip trip = event.getData();
            LOG.info("Create trip with ID: {}", trip.getTripId());
            service.createTrip(trip);
            break;

        case DELETE:
            int tripId = event.getKey();
            LOG.info("Delete history with tripID: {}", tripId);
            service.deleteTrip(tripId);
            break;

        default:
            String errorMessage = "Incorrect event type: " + event.getEventType() + ", expected a CREATE or DELETE event";
            LOG.warn(errorMessage);
            throw new EventProcessingException(errorMessage);
        }

        LOG.info("Message processing done!");
    }
}
