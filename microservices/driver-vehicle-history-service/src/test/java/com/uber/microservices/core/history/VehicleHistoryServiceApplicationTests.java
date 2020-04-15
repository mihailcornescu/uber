package com.uber.microservices.core.history;

import com.uber.api.core.driver.Driver;
import com.uber.api.core.history.History;
import com.uber.api.event.Event;
import com.uber.microservices.core.history.persistence.HistoryRepository;
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
import com.uber.util.exceptions.InvalidInputException;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"spring.data.mongodb.port: 0", "eureka.client.enabled=false"})
public class VehicleHistoryServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private HistoryRepository repository;

	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;

	@Before
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll().block();
	}

	@Test
	public void getDriverHistoryByDriverId() {

		int driverId = 1;

		sendCreateHistoryEvent(driverId, 1);
		sendCreateHistoryEvent(driverId, 2);
		sendCreateHistoryEvent(driverId, 3);

		assertEquals(3, (long)repository.findByDriverId(driverId).count().block());

		getAndVeryHistoryByDriverId(driverId, OK)
			.jsonPath("$.length()").isEqualTo(3)
			.jsonPath("$[2].driverId").isEqualTo(driverId)
			.jsonPath("$[2].vehicleId").isEqualTo(3);
	}

	@Test
	public void duplicateError() {

		int driverId = 1;
		int vehicleId = 1;

		sendCreateHistoryEvent(driverId, vehicleId);

		assertEquals(1, (long)repository.count().block());

		try {
			sendCreateHistoryEvent(driverId, vehicleId);
			fail("Expected a MessagingException here!");
		} catch (MessagingException me) {
			if (me.getCause() instanceof InvalidInputException)	{
				InvalidInputException iie = (InvalidInputException)me.getCause();
				assertEquals("Duplicate key, Vehicle Id: 1, History Id:1", iie.getMessage());
			} else {
				fail("Expected a InvalidInputException as the root cause!");
			}
		}

		assertEquals(1, (long)repository.count().block());
	}

	@Test
	public void deleteDriverHistory() {

		int driverId = 1;
		int vehicleId = 1;

		sendCreateHistoryEvent(driverId, vehicleId);
		assertEquals(1, (long)repository.findByDriverId(driverId).count().block());

		sendDeleteHistoryEvent(driverId);
		assertEquals(0, (long)repository.findByDriverId(driverId).count().block());

		sendDeleteHistoryEvent(driverId);
	}

	@Test
	public void getDriverHistoryMissingParameter() {

		getAndVeryHistoryByDriverId("", BAD_REQUEST)
			.jsonPath("$.path").isEqualTo("/driver-history")
			.jsonPath("$.message").isEqualTo("Required int parameter 'driverId' is not present");
	}

	@Test
	public void getDriverHistoryInvalidParameter() {

		getAndVeryHistoryByDriverId("?driverId=no-integer", BAD_REQUEST)
			.jsonPath("$.path").isEqualTo("/driver-history")
			.jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	public void getDriverHistoryNotFound() {

		getAndVeryHistoryByDriverId("?driverId=113", OK)
			.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	public void getHistoryInvalidParameterNegativeValue() {

		int driverIdInvalid = -1;

		getAndVeryHistoryByDriverId("?driverId=" + driverIdInvalid, UNPROCESSABLE_ENTITY)
			.jsonPath("$.path").isEqualTo("/driver-history")
			.jsonPath("$.message").isEqualTo("Invalid driverId: " + driverIdInvalid);
	}

	private WebTestClient.BodyContentSpec getAndVeryHistoryByDriverId(int driverId, HttpStatus expectedStatus) {
		return getAndVeryHistoryByDriverId("?driverId=" + driverId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVeryHistoryByDriverId(String driverIdQuery, HttpStatus expectedStatus) {
		return client.get()
			.uri("/driver-history" + driverIdQuery)
			.accept(APPLICATION_JSON_UTF8)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON_UTF8)
			.expectBody();
	}

	private void sendCreateHistoryEvent(int driverId, int vehicleId) {
		History history = new History(driverId, vehicleId, LocalDate.now(), LocalDate.now());
		Event<Integer, Driver> event = new Event(Event.Type.CREATE, driverId, history);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteHistoryEvent(int driverId) {
		Event<Integer, Driver> event = new Event(Event.Type.DELETE, driverId, null);
		input.send(new GenericMessage<>(event));
	}
}
