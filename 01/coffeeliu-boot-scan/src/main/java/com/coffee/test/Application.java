package com.coffee.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.coffee.testscan.TestScan;

@SpringBootApplication
//@ComponentScan("com.coffee.testscan")
public class Application {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(Application.class, args);
        TestScan componentScan = run.getBean(TestScan.class);
        System.out.println(componentScan.toString());
    }
}