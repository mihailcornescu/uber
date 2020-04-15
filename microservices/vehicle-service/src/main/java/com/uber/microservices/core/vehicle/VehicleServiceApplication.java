package com.uber.microservices.core.vehicle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.uber")
public class VehicleServiceApplication {

	private static final Logger LOG = LoggerFactory.getLogger(VehicleServiceApplication.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext ctx = SpringApplication.run(VehicleServiceApplication.class, args);

		String mongodDbHost = ctx.getEnvironment().getProperty("spring.data.mongodb.host");
		String mongodDbPort = ctx.getEnvironment().getProperty("spring.data.mongodb.port");
		LOG.info("Connected to MongoDb: " + mongodDbHost + ":" + mongodDbPort);
	}
}
