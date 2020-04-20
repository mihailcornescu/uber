package com.uber.microservices.core.trip;

import com.uber.api.core.trip.Trip;
import com.uber.microservices.core.trip.persistence.TripRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.http.HttpStatus;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.uber.api.event.Event;
import com.uber.util.exceptions.InvalidInputException;

import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static com.uber.api.event.Event.Type.CREATE;
import static com.uber.api.event.Event.Type.DELETE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"spring.data.mongodb.port: 0", "eureka.client.enabled=false"})
public class TripServiceApplicationTests {

    @Autowired
    private WebTestClient client;

	@Autowired
	private TripRepository repository;

	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;

	@Before
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll();
	}

	@Test
	public void getByTripId() {

		int tripId = 1;

		assertFalse(repository.findByTripId(tripId).isPresent());
		assertEquals(0, (long)repository.count());

		sendCreateTripEvent(tripId);

		assertNotNull(repository.findByTripId(tripId));
		assertEquals(1, (long)repository.count());

		getAndVerifyTrip("/" + tripId, OK)
            .jsonPath("$.tripId").isEqualTo(tripId);
	}

	@Test
	public void duplicateError() {

		int tripId = 1;

		assertFalse(repository.findByTripId(tripId).isPresent());

		sendCreateTripEvent(tripId);

		assertNotNull(repository.findByTripId(tripId));

		try {
			sendCreateTripEvent(tripId);
			fail("Expected a MessagingException here!");
		} catch (MessagingException me) {
			if (me.getCause() instanceof InvalidInputException)	{
				InvalidInputException iie = (InvalidInputException)me.getCause();
				assertEquals("Duplicate key, Trip Id: " + tripId, iie.getMessage());
			} else {
				fail("Expected a InvalidInputException as the root cause!");
			}
		}
	}

	@Test
	public void deleteTrip() {

		int tripId = 1;

		sendCreateTripEvent(tripId);
		assertNotNull(repository.findByTripId(tripId));

		sendDeleteTripEvent(tripId);
		assertFalse(repository.findByTripId(tripId).isPresent());

		sendDeleteTripEvent(tripId);
	}

	@Test
	public void getTripInvalidParameterString() {

		getAndVerifyTrip("/no-integer", BAD_REQUEST)
            .jsonPath("$.path").isEqualTo("/trip/no-integer")
            .jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	public void getTripNotFound() {

		int tripIdNotFound = 13;
		getAndVerifyTrip(tripIdNotFound, NOT_FOUND)
            .jsonPath("$.path").isEqualTo("/trip/" + tripIdNotFound)
            .jsonPath("$.message").isEqualTo("No trip found for tripId: " + tripIdNotFound);
	}

	@Test
	public void getTripInvalidParameterNegativeValue() {

        int tripIdInvalid = -1;

		getAndVerifyTrip(tripIdInvalid, UNPROCESSABLE_ENTITY)
            .jsonPath("$.path").isEqualTo("/trip/" + tripIdInvalid)
            .jsonPath("$.message").isEqualTo("Invalid tripId: " + tripIdInvalid);
	}

	private WebTestClient.BodyContentSpec getAndVerifyTrip(int tripId, HttpStatus expectedStatus) {
		return getAndVerifyTrip("/" + tripId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVerifyTrip(String tripIdPath, HttpStatus expectedStatus) {
		return client.get()
			.uri("/trip" + tripIdPath)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private void sendCreateTripEvent(int tripId) {
		Trip trip = new Trip(tripId, "startLoc " + tripId, "endLoc " + tripId);
		Event<Integer, Trip> event = new Event(CREATE, tripId, trip);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteTripEvent(int tripId) {
		Event<Integer, Trip> event = new Event(DELETE, tripId, null);
		input.send(new GenericMessage<>(event));
	}
}