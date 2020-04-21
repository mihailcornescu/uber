package com.uber.microservices.core.trip;

import com.uber.microservices.core.trip.persistence.TripRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.http.HttpStatus;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.uber.api.core.trip.Trip;
import com.uber.api.event.Event;

import static com.uber.api.event.Event.Type.CREATE;
import static com.uber.api.event.Event.Type.DELETE;
import static org.junit.Assert.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;
import static reactor.core.publisher.Mono.just;

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
		repository.deleteAll();
	}

	@Test
	public void getTripsByDriverId() {

		int driverId = 1;

		postAndVerifyTrip(driverId, 1, OK);
		postAndVerifyTrip(driverId, 2, OK);
		postAndVerifyTrip(driverId, 3, OK);

		assertEquals(3, repository.findByDriverId(driverId).size());

		getAndVerifyTripsByDriverId(driverId, OK)
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[2].driverId").isEqualTo(driverId)
				.jsonPath("$[2].tripId").isEqualTo(3);
	}

	@Test
	public void duplicateError() {

		int driverId = 1;
		int tripId = 1;

		postAndVerifyTrip(driverId, tripId, OK)
				.jsonPath("$.driverId").isEqualTo(driverId)
				.jsonPath("$.tripId").isEqualTo(tripId);

		assertEquals(1, repository.count());

		postAndVerifyTrip(driverId, tripId, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/trip")
				.jsonPath("$.message").isEqualTo("Duplicate key, Driver Id: 1, Trip Id:1");

		assertEquals(1, repository.count());
	}

	@Test
	public void deleteTrips() {

		int driverId = 1;
		int tripId = 1;

		postAndVerifyTrip(driverId, tripId, OK);
		assertEquals(1, repository.findByDriverId(driverId).size());

		deleteAndVerifyTripsByDriverId(driverId, OK);
		assertEquals(0, repository.findByDriverId(driverId).size());

		deleteAndVerifyTripsByDriverId(driverId, OK);
	}

	@Test
	public void getTripsMissingParameter() {

		getAndVerifyTripsByDriverId("", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/trips")
				.jsonPath("$.message").isEqualTo("Required int parameter 'driverId' is not present");
	}

	@Test
	public void getTripsInvalidParameter() {

		getAndVerifyTripsByDriverId("?driverId=no-integer", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/trips")
				.jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	public void getTripsNotFound() {

		getAndVerifyTripsByDriverId("?driverId=113", OK)
				.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	public void getTripsInvalidParameterNegativeValue() {

		int driverIdInvalid = -1;

		getAndVerifyTripsByDriverId("?driverId=" + driverIdInvalid, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/trips")
				.jsonPath("$.message").isEqualTo("Invalid driverId: " + driverIdInvalid);
	}

	private WebTestClient.BodyContentSpec getAndVerifyTripsByDriverId(int driverId, HttpStatus expectedStatus) {
		return getAndVerifyTripsByDriverId("?driverId=" + driverId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVerifyTripsByDriverId(String driverIdQuery, HttpStatus expectedStatus) {
		return client.get()
				.uri("/trips" + driverIdQuery)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private WebTestClient.BodyContentSpec postAndVerifyTrip(int driverId, int tripId, HttpStatus expectedStatus) {
		Trip trip = new Trip().setDriverId(driverId).setTripId(tripId).setStartLocation("sl1")
				.setEndLocation("el1");;
		return client.post()
				.uri("/trip")
				.body(just(trip), Trip.class)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private WebTestClient.BodyContentSpec deleteAndVerifyTripsByDriverId(int driverId, HttpStatus expectedStatus) {
		return client.delete()
				.uri("/trip?driverId=" + driverId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectBody();
	}


	private void sendCreateTripEvent(int driverId, int tripId) {
		Trip trip = new Trip().setDriverId(1).setTripId(2).setStartLocation("sl1")
				.setEndLocation("el1");;
		Event<Integer, Trip> event = new Event(CREATE, driverId, trip);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteTripEvent(int driverId) {
		Event<Integer, Trip> event = new Event(DELETE, driverId, null);
		input.send(new GenericMessage<>(event));
	}
}
