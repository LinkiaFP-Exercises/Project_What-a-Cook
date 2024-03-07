package com.whatacook.cookers.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Slf4j
@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @PostConstruct
    public void init() {
        try {

            String URI = "MONGO_URI_WHATACOOK_USERS";

            String mongoUri = DockerConfig.readSecret(URI);

            if (StringUtils.hasText(mongoUri))
                System.setProperty(URI, mongoUri.trim());

            log.warn("Mongo URI : " + StringUtils.hasText(mongoUri));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
