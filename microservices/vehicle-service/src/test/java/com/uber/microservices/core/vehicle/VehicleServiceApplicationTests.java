package com.uber.microservices.core.vehicle;

import com.uber.api.core.vehicle.Vehicle;
import com.uber.microservices.core.vehicle.persistence.VehicleRepository;
import com.uber.util.Constants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.http.HttpStatus;
import org.springframework.integration.channel.AbstractMessageChannel;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;
import static reactor.core.publisher.Mono.just;

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
		repository.deleteAll();
	}

	@Test
	public void getDriverVehicleHistoryByDriverId() {

		int driverId = 1;

		postAndVerifyDriverVehicleHistory(driverId, 1, OK);
		postAndVerifyDriverVehicleHistory(driverId, 2, OK);
		postAndVerifyDriverVehicleHistory(driverId, 3, OK);

		assertEquals(3, repository.findByDriverId(driverId).size());

		getAndVerifyDriverVehicleHistoryByDriverId(driverId, OK)
				.jsonPath("$.length()").isEqualTo(3)
				.jsonPath("$[2].driverId").isEqualTo(driverId)
				.jsonPath("$[2].vehicleId").isEqualTo(3);
	}

	@Test
	public void duplicateError() {

		int driverId = 1;
		int vehicleId = 1;

		postAndVerifyDriverVehicleHistory(driverId, vehicleId, OK)
				.jsonPath("$.driverId").isEqualTo(driverId)
				.jsonPath("$.vehicleId").isEqualTo(vehicleId);

		assertEquals(1, repository.count());

		postAndVerifyDriverVehicleHistory(driverId, vehicleId, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/driver-vehicle-history")
				.jsonPath("$.message").isEqualTo("Duplicate key, Driver Id: 1, Vehicle Id:1");

		assertEquals(1, repository.count());
	}

	@Test
	public void deleteDriversVehicleHistory() {

		int driverId = 1;
		int vehicleId = 1;

		postAndVerifyDriverVehicleHistory(driverId, vehicleId, OK);
		assertEquals(1, repository.findByDriverId(driverId).size());

		deleteAndVerifyDriverVehicleHistoryByDriverId(driverId, OK);
		assertEquals(0, repository.findByDriverId(driverId).size());

		deleteAndVerifyDriverVehicleHistoryByDriverId(driverId, OK);
	}

	@Test
	public void getDriverVehicleHistoryMissingParameter() {

		getAndVerifyDriverVehicleHistoryByDriverId("", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/driver-vehicle-history")
				.jsonPath("$.message").isEqualTo("Required int parameter 'driverId' is not present");	}

	@Test
	public void getDriverVehicleHistoryInvalidParameter() {

		getAndVerifyDriverVehicleHistoryByDriverId("?driverId=no-integer", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/driver-vehicle-history")
				.jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	public void getDriversVehicleHistoryNotFound() {

		getAndVerifyDriverVehicleHistoryByDriverId("?driverId=113", OK)
				.jsonPath("$.length()").isEqualTo(0);
	}

	@Test
	public void getDriverVehicleHistoryInvalidParameterNegativeValue() {

		int driverIdInvalid = -1;

		getAndVerifyDriverVehicleHistoryByDriverId("?driverId=" + driverIdInvalid, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/driver-vehicle-history")
				.jsonPath("$.message").isEqualTo("Invalid driverId: " + driverIdInvalid);
	}

	private WebTestClient.BodyContentSpec getAndVerifyDriverVehicleHistoryByDriverId(int driverId, HttpStatus expectedStatus) {
		return getAndVerifyDriverVehicleHistoryByDriverId("?driverId=" + driverId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVerifyDriverVehicleHistoryByDriverId(String driverIdQuery, HttpStatus expectedStatus) {
		return client.get()
				.uri("/driver-vehicle-history" + driverIdQuery)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private WebTestClient.BodyContentSpec postAndVerifyDriverVehicleHistory(int driverId, int vehicleId, HttpStatus expectedStatus) {
		Vehicle history = new Vehicle().setDriverId(driverId).setVehicleId(vehicleId).setDateFrom(LocalDate.now())
				.setDateFrom(Constants.END_OF_DATE);
		return client.post()
				.uri("/driver-vehicle-history")
				.body(just(history), Vehicle.class)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private WebTestClient.BodyContentSpec deleteAndVerifyDriverVehicleHistoryByDriverId(int driverId, HttpStatus expectedStatus) {
		return client.delete()
				.uri("/driver-vehicle-history?driverId=" + driverId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectBody();
	}
}
