package com.whatacook.cookers.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @PostConstruct
    public void init() {
        try {
            String USER = "MONGODB_USER_CLUSTER";
            String PASS = "MONGODB_USER_CLUSTER";
            String BBDD = "MONGODB_USER_CLUSTER";

            String mongoUser = DockerConfig.readSecret(USER);
            String mongoPass = DockerConfig.readSecret(PASS);
            String mongoDb = DockerConfig.readSecret(BBDD);

            if (StringUtils.hasText(mongoUser) && StringUtils.hasText(mongoPass) && StringUtils.hasText(mongoDb)) {
                System.setProperty("MONGODB_USER_CLUSTER", mongoUser.trim());
                System.setProperty("MONGODB_PASSWORD_CLUSTER", mongoPass.trim());
                System.setProperty("MONGODB_USERS_DATABASE", mongoDb.trim());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
