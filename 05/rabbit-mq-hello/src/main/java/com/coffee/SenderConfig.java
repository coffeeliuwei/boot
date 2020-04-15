package com.coffee;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SenderConfig {
	
	@Bean
	public Queue aganqueue(){
		return new Queue("hello-queue");
	}
}
