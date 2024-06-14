package com.whatacook.cookers.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * Configuration class for setting up MongoDB connection.
 * Reads the MongoDB URI from Docker secrets and sets the system property.
 * <p>
 * Annotations:
 * - @Slf4j: Lombok annotation to generate a logger field.
 * - @Configuration: Indicates that this class contains Spring configuration.
 * <p>
 * Methods:
 * - init(): Initializes the MongoDB URI by reading Docker secrets.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@Slf4j
@Configuration
public class MongoConfig {

    /**
     * Initializes the MongoDB URI by reading Docker secrets.
     * Sets the system property for the MongoDB URI.
     */
    @PostConstruct
    public void init() {
        try {
            String URI = "MONGO_URI_WHATACOOK_USERS";

            String mongoUri = DockerConfig.readSecret(URI);

            if (StringUtils.hasText(mongoUri))
                System.setProperty(URI, mongoUri.trim());

            log.warn("Mongo URI : {}", StringUtils.hasText(mongoUri));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
