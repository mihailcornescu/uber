package com.uber.microservices.core.vehicle;

import com.uber.microservices.core.vehicle.persistence.VehicleRepository;
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
import com.uber.api.core.vehicle.Vehicle;
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
public class VehicleServiceApplicationTests {

    @Autowired
    private WebTestClient client;

	@Autowired
	private VehicleRepository repository;

	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;

	@Before
	public void setupDb() {
		input = (AbstractMessageChannel) channels.input();
		repository.deleteAll();
	}

	@Test
	public void getVehicleById() {

		int vehicleId = 1;

		assertFalse(repository.findByVehicleId(vehicleId).isPresent());
		assertEquals(0, (long)repository.count());

		sendCreateVehicleEvent(vehicleId);

		assertNotNull(repository.findByVehicleId(vehicleId));
		assertEquals(1, (long)repository.count());

		getAndVerifyVehicle("/" + String.valueOf(vehicleId), OK)
            .jsonPath("$.vehicleId").isEqualTo(vehicleId);
	}

	@Test
	public void duplicateError() {

		int vehicleId = 1;

		assertFalse(repository.findByVehicleId(vehicleId).isPresent());

		sendCreateVehicleEvent(vehicleId);

		assertNotNull(repository.findByVehicleId(vehicleId));

		try {
			sendCreateVehicleEvent(vehicleId);
			fail("Expected a MessagingException here!");
		} catch (MessagingException me) {
			if (me.getCause() instanceof InvalidInputException)	{
				InvalidInputException iie = (InvalidInputException)me.getCause();
				assertEquals("Duplicate key, Vehicle Id: " + vehicleId, iie.getMessage());
			} else {
				fail("Expected a InvalidInputException as the root cause!");
			}
		}
	}

	@Test
	public void deleteVehicle() {

		int vehicleId = 1;

		sendCreateVehicleEvent(vehicleId);
		assertNotNull(repository.findByVehicleId(vehicleId));

		sendDeleteVehicleEvent(vehicleId);
		assertFalse(repository.findByVehicleId(vehicleId).isPresent());

		sendDeleteVehicleEvent(vehicleId);
	}

	@Test
	public void getVehicleInvalidParameterString() {

		getAndVerifyVehicle("/no-integer", BAD_REQUEST)
            .jsonPath("$.path").isEqualTo("/vehicle/no-integer")
            .jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	public void getVehicleNotFound() {

		int vehicleIdNotFound = 13;
		getAndVerifyVehicle(vehicleIdNotFound, NOT_FOUND)
            .jsonPath("$.path").isEqualTo("/vehicle/" + vehicleIdNotFound)
            .jsonPath("$.message").isEqualTo("No vehicle found for vehicleId: " + vehicleIdNotFound);
	}

	@Test
	public void getVehicleInvalidParameterNegativeValue() {

        int vehicleIdInvalid = -1;

		getAndVerifyVehicle(vehicleIdInvalid, UNPROCESSABLE_ENTITY)
            .jsonPath("$.path").isEqualTo("/vehicle/" + vehicleIdInvalid)
            .jsonPath("$.message").isEqualTo("Invalid vehicleId: " + vehicleIdInvalid);
	}

	private WebTestClient.BodyContentSpec getAndVerifyVehicle(int vehicleId, HttpStatus expectedStatus) {
		return getAndVerifyVehicle("/" + vehicleId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVerifyVehicle(String vehicleIdPath, HttpStatus expectedStatus) {
		return client.get()
			.uri("vehicle" + vehicleIdPath)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private void sendCreateVehicleEvent(int vehicleId) {
		Vehicle vehicle = new Vehicle(vehicleId, "Name " + vehicleId, "color" + vehicleId, "registrationNumber" + vehicleId);
		Event<Integer, Vehicle> event = new Event(CREATE, vehicleId, vehicle);
		input.send(new GenericMessage<>(event));
	}

	private void sendDeleteVehicleEvent(int vehicleId) {
		Event<Integer, Vehicle> event = new Event(DELETE, vehicleId, null);
		input.send(new GenericMessage<>(event));
	}
}