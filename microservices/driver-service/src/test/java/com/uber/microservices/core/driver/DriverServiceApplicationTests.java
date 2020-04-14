package com.uber.microservices.core.driver;

import com.uber.microservices.core.driver.persistence.DriverRepository;
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
import com.uber.api.core.driver.Driver;
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
public class DriverServiceApplicationTests {

    @Autowired
    private WebTestClient client;

	@Autowired
	private DriverRepository repository;

	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;

	@Before
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll();
	}

	@Test
	public void getDriverById() {

		int driverId = 1;

		assertFalse(repository.findByDriverId(driverId).isPresent());
		assertEquals(0, (long)repository.count());

		sendCreateDriverEvent(driverId);

		assertNotNull(repository.findByDriverId(driverId));
		assertEquals(1, (long)repository.count());

		getAndVerifyDriver("/" + String.valueOf(driverId), OK)
            .jsonPath("$.driverId").isEqualTo(driverId);
	}

	@Test
	public void duplicateError() {

		int driverId = 1;

		assertFalse(repository.findByDriverId(driverId).isPresent());

		sendCreateDriverEvent(driverId);

		assertNotNull(repository.findByDriverId(driverId));

		try {
			sendCreateDriverEvent(driverId);
			fail("Expected a MessagingException here!");
		} catch (MessagingException me) {
			if (me.getCause() instanceof InvalidInputException)	{
				InvalidInputException iie = (InvalidInputException)me.getCause();
				assertEquals("Duplicate key, Driver Id: " + driverId, iie.getMessage());
			} else {
				fail("Expected a InvalidInputException as the root cause!");
			}
		}
	}

	@Test
	public void deleteDriver() {

		int driverId = 1;

		sendCreateDriverEvent(driverId);
		assertNotNull(repository.findByDriverId(driverId));

		sendDeleteDriverEvent(driverId);
		assertFalse(repository.findByDriverId(driverId).isPresent());

		sendDeleteDriverEvent(driverId);
	}

	@Test
	public void getDriverInvalidParameterString() {

		getAndVerifyDriver("/no-integer", BAD_REQUEST)
            .jsonPath("$.path").isEqualTo("/driver/no-integer")
            .jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	public void getDriverNotFound() {

		int driverIdNotFound = 13;
		getAndVerifyDriver(driverIdNotFound, NOT_FOUND)
            .jsonPath("$.path").isEqualTo("/driver/" + driverIdNotFound)
            .jsonPath("$.message").isEqualTo("No driver found for driverId: " + driverIdNotFound);
	}

	@Test
	public void getDriverInvalidParameterNegativeValue() {

        int driverIdInvalid = -1;

		getAndVerifyDriver(driverIdInvalid, UNPROCESSABLE_ENTITY)
            .jsonPath("$.path").isEqualTo("/driver/" + driverIdInvalid)
            .jsonPath("$.message").isEqualTo("Invalid driverId: " + driverIdInvalid);
	}

	private WebTestClient.BodyContentSpec getAndVerifyDriver(int driverId, HttpStatus expectedStatus) {
		return getAndVerifyDriver("/" + driverId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVerifyDriver(String driverIdPath, HttpStatus expectedStatus) {
		return client.get()
			.uri("/driver" + driverIdPath)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private void sendCreateDriverEvent(int driverID) {
		Driver driver = new Driver(driverID, "Name " + driverID, "0732", "0732");
		Event<Integer, Driver> event = new Event(CREATE, driverID, driver);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteDriverEvent(int driverId) {
		Event<Integer, Driver> event = new Event(DELETE, driverId, null);
		input.send(new GenericMessage<>(event));
	}
}