package com.coffee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import liu.wei.Person;

@SpringBootApplication
public class CoffeeliuBootSpringfactoriesApplication {

	public static void main(String[] args) {
	Person person=	SpringApplication.run(CoffeeliuBootSpringfactoriesApplication.class, args)
						.getBean(Person.class);
	System.out.println(person.say());
	}

}
