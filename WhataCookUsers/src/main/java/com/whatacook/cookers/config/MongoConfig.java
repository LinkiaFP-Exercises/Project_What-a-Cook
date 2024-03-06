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
            String PASS = "MONGODB_PASSWORD_CLUSTER";
            String BBDD = "MONGODB_USERS_DATABASE";
            String URI = "MONGO_URI_WAC_F_USERS";

            String mongoUser = DockerConfig.readSecret(USER);
            String mongoPass = DockerConfig.readSecret(PASS);
            String mongoDb = DockerConfig.readSecret(BBDD);
            String mongoUri = DockerConfig.readSecret(URI);

            if (StringUtils.hasText(mongoUser) && StringUtils.hasText(mongoPass) && StringUtils.hasText(mongoDb)) {
                System.setProperty("MONGODB_USER_CLUSTER", mongoUser.trim());
                System.setProperty("MONGODB_PASSWORD_CLUSTER", mongoPass.trim());
                System.setProperty("MONGODB_USERS_DATABASE", mongoDb.trim());
                System.setProperty("MONGO_URI_WAC_F_USERS", mongoUri.trim());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
