package com.uber.microservices.core.payment;

import com.uber.api.core.payment.Payment;
import com.uber.microservices.core.payment.persistence.PaymentRepository;
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
import com.uber.api.event.Event;

import java.time.LocalDateTime;

import static com.uber.api.event.Event.Type.CREATE;
import static com.uber.api.event.Event.Type.DELETE;
import static org.junit.Assert.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.*;
import static reactor.core.publisher.Mono.just;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=RANDOM_PORT, properties = {"spring.data.mongodb.port: 0", "eureka.client.enabled=false"})
public class PaymentServiceApplicationTests {

	@Autowired
	private WebTestClient client;

	@Autowired
	private PaymentRepository repository;

	@Autowired
	private Sink channels;

	private AbstractMessageChannel input = null;

	@Before
	public void setupDb() {
		repository.deleteAll();
	}

	@Test
	public void getPaymentsByProductId() {

		int tripId = 1;

		postAndVerifyPayment(tripId, 1, OK);

		assertNotNull(repository.findByPaymentId(tripId));

		getAndVerifyPaymentsByProductId(tripId, OK)
				.jsonPath("$.tripId").isEqualTo(tripId)
				.jsonPath("$.paymentId").isEqualTo(1);
	}

	@Test
	public void duplicateError() {

		int tripId = 1;
		int paymentId = 1;

		postAndVerifyPayment(tripId, paymentId, OK)
				.jsonPath("$.tripId").isEqualTo(tripId)
				.jsonPath("$.paymentId").isEqualTo(paymentId);

		assertEquals(1, repository.count());

		postAndVerifyPayment(tripId, paymentId, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/payment")
				.jsonPath("$.message").isEqualTo("Duplicate key, Product Id: 1, Payment Id:1");

		assertEquals(1, repository.count());
	}

	@Test
	public void deletePayments() {

		int tripId = 1;
		int paymentId = 1;

		postAndVerifyPayment(tripId, paymentId, OK);
		assertNotNull(repository.findByPaymentId(tripId));

		deleteAndVerifyPaymentsByProductId(tripId, OK);
		assertNull(repository.findByPaymentId(tripId));
	}

	@Test
	public void getPaymentsMissingParameter() {

		getAndVerifyPaymentsByProductId("", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/payment")
				.jsonPath("$.message").isEqualTo("Required int parameter 'tripId' is not present");
	}

	@Test
	public void getPaymentsInvalidParameter() {

		getAndVerifyPaymentsByProductId("?tripId=no-integer", BAD_REQUEST)
				.jsonPath("$.path").isEqualTo("/payment")
				.jsonPath("$.message").isEqualTo("Type mismatch.");
	}

	@Test
	public void getPaymentsInvalidParameterNegativeValue() {

		int tripIdInvalid = -1;

		getAndVerifyPaymentsByProductId("?tripId=" + tripIdInvalid, UNPROCESSABLE_ENTITY)
				.jsonPath("$.path").isEqualTo("/payment")
				.jsonPath("$.message").isEqualTo("Invalid tripId: " + tripIdInvalid);
	}

	private WebTestClient.BodyContentSpec getAndVerifyPaymentsByProductId(int tripId, HttpStatus expectedStatus) {
		return getAndVerifyPaymentsByProductId("?tripId=" + tripId, expectedStatus);
	}

	private WebTestClient.BodyContentSpec getAndVerifyPaymentsByProductId(String tripIdQuery, HttpStatus expectedStatus) {
		return client.get()
				.uri("/payment" + tripIdQuery)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private WebTestClient.BodyContentSpec postAndVerifyPayment(int tripId, int paymentId, HttpStatus expectedStatus) {
		Payment payment = new Payment(tripId, paymentId, paymentId, paymentId, LocalDateTime.now(), LocalDateTime.now());
		return client.post()
				.uri("/payment")
				.body(just(payment), Payment.class)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectHeader().contentType(APPLICATION_JSON)
				.expectBody();
	}

	private WebTestClient.BodyContentSpec deleteAndVerifyPaymentsByProductId(int tripId, HttpStatus expectedStatus) {
		return client.delete()
				.uri("/payment?tripId=" + tripId)
				.accept(APPLICATION_JSON)
				.exchange()
				.expectStatus().isEqualTo(expectedStatus)
				.expectBody();
	}


	private void sendCreatePaymentEvent(int tripID, int paymentId) {
		Payment payment = new Payment(tripID, paymentId, paymentId, paymentId, LocalDateTime.now(), LocalDateTime.now());
		Event<Integer, Payment> event = new Event(CREATE, tripID, payment);
		input.send(new GenericMessage<>(event));
	}
}
