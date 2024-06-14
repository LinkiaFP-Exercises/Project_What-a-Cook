package linkia.dam.whatacookrecipes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the What-a-Cook Recipes application.
 * This class serves as the entry point for the Spring Boot application.
 * <p>
 * Annotations:
 * - @SpringBootApplication: Indicates that this class is the main entry point for the Spring Boot application and enables auto-configuration, component scanning, and configuration properties.
 * <p>
 * Methods:
 * - main(String[] args): The main method that runs the Spring Boot application.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@SpringBootApplication
public class WhataCookRecipesApplication {

    /**
     * The main method that runs the Spring Boot application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(WhataCookRecipesApplication.class, args);
    }

}
