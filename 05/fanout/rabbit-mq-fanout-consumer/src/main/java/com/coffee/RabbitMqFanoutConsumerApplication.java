package com.coffee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RabbitMqFanoutConsumerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RabbitMqFanoutConsumerApplication.class, args);
	}

}
