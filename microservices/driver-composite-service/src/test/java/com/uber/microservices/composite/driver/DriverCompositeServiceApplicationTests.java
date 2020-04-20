package com.uber.microservices.composite.driver;

import com.uber.api.composite.driver.DriverAggregate;
import com.uber.api.core.driver.Driver;
import com.uber.api.core.drivervehiclehistory.DriverVehicleHistory;
import com.uber.api.core.trip.Trip;
import com.uber.microservices.composite.driver.services.DriverCompositeIntegration;
import com.uber.util.Constants;
import com.uber.util.exceptions.InvalidInputException;
import com.uber.util.exceptions.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;
import static reactor.core.publisher.Mono.just;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT)
public class DriverCompositeServiceApplicationTests {

	private static final int DRIVER_ID_OK = 1;
	private static final int DRIVER_ID_NOT_FOUND = 2;
	private static final int DRIVER_ID_INVALID = 3;

    @Autowired
    private WebTestClient client;

	@MockBean
	private DriverCompositeIntegration compositeIntegration;

	@Before
	public void setUp() {

		when(compositeIntegration.getDriver(DRIVER_ID_OK)).
			thenReturn(new Driver(DRIVER_ID_OK, "name", "phone"));
		when(compositeIntegration.getVechileHistory(DRIVER_ID_OK)).
			thenReturn(singletonList(new DriverVehicleHistory(DRIVER_ID_OK, 1, LocalDate.now(), Constants.END_OF_DATE)));
		when(compositeIntegration.getTrips(DRIVER_ID_OK)).
				thenReturn(singletonList(new Trip().setDriverId(DRIVER_ID_OK).setTripId(1).setStartLocation("sl1").setEndLocation("sl2")));

		when(compositeIntegration.getDriver(DRIVER_ID_NOT_FOUND)).thenThrow(new NotFoundException("NOT FOUND: " + DRIVER_ID_NOT_FOUND));

		when(compositeIntegration.getDriver(DRIVER_ID_INVALID)).thenThrow(new InvalidInputException("INVALID: " + DRIVER_ID_INVALID));
	}

	@Test
	public void contextLoads() {
	}

	@Test
	public void getDriverById() {

		getAndVerifyDriver(DRIVER_ID_OK, OK)
            .jsonPath("$.driverId").isEqualTo(DRIVER_ID_OK)
            .jsonPath("$.vehicles.length()").isEqualTo(1)
			.jsonPath("$.trips.length()").isEqualTo(1);
	}

	@Test
	public void getDriverNotFound() {

		getAndVerifyDriver(DRIVER_ID_NOT_FOUND, NOT_FOUND)
            .jsonPath("$.path").isEqualTo("/driver-composite/" + DRIVER_ID_NOT_FOUND)
            .jsonPath("$.message").isEqualTo("NOT FOUND: " + DRIVER_ID_NOT_FOUND);
	}

	@Test
	public void getDriverInvalidInput() {

		getAndVerifyDriver(DRIVER_ID_INVALID, UNPROCESSABLE_ENTITY)
            .jsonPath("$.path").isEqualTo("/driver-composite/" + DRIVER_ID_INVALID)
            .jsonPath("$.message").isEqualTo("INVALID: " + DRIVER_ID_INVALID);
	}

	private WebTestClient.BodyContentSpec getAndVerifyDriver(int driverId, HttpStatus expectedStatus) {
		return client.get()
			.uri("/driver-composite/" + driverId)
			.accept(APPLICATION_JSON)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus)
			.expectHeader().contentType(APPLICATION_JSON)
			.expectBody();
	}

	private void postAndVerifyDriver(DriverAggregate compositeDriver, HttpStatus expectedStatus) {
		client.post()
			.uri("/driver-composite")
			.body(just(compositeDriver), DriverAggregate.class)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus);
	}

	private void deleteAndVerifyDriver(int driverId, HttpStatus expectedStatus) {
		client.delete()
			.uri("/driver-composite/" + driverId)
			.exchange()
			.expectStatus().isEqualTo(expectedStatus);
	}
}
