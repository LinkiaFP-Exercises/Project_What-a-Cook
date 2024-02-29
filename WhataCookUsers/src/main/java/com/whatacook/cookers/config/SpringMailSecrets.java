package com.whatacook.cookers.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Configuration
public class SpringMailSecrets {

    @PostConstruct
    public void init() {

        try {
            String USER = "SPRING_MAIL_VALIDATION";
            String PASS = "SPRING_MAIL_PASSWORD";

            String springMailUser = DockerSecrets.readSecret(USER, USER);
            String springMailPass = DockerSecrets.readSecret(PASS, PASS);

            if (StringUtils.hasText(springMailUser) && StringUtils.hasText(springMailPass)) {
                System.setProperty("SPRING_MAIL_VALIDATION", springMailUser.trim());
                System.setProperty("SPRING_MAIL_PASSWORD", springMailPass.trim());
            }
        } catch (IOException e) {  throw new RuntimeException(e); }

    }
}
