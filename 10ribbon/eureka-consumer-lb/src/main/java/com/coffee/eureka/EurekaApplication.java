package com.coffee.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.ZoneAvoidanceRule;

@EnableEurekaClient
@SpringBootApplication
public class EurekaApplication {
//    @Bean
//	IRule ribbonIRule()
//	{
//		return new ZoneAvoidanceRule();
//	}
	public static void main(String[] args) {
		SpringApplication.run(EurekaApplication.class, args);
	}
}
