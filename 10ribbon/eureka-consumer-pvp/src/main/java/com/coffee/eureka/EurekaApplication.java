package com.coffee.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.ZoneAvoidanceRule;


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
