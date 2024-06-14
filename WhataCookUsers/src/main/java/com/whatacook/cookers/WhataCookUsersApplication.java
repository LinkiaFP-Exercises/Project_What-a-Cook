package com.whatacook.cookers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for the WhataCook Users Application.
 * This class initializes and runs the Spring Boot application.
 * <p>
 * Annotations:
 * - @SpringBootApplication: Indicates that this is a Spring Boot application.
 * <p>
 * Methods:
 * - main(String[] args): Main method that serves as the entry point of the application.
 * It runs the Spring Boot application using SpringApplication.run.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@SpringBootApplication
public class WhataCookUsersApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhataCookUsersApplication.class, args);
    }

}
