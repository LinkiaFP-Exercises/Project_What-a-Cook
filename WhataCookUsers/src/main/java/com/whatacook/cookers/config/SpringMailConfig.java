package com.whatacook.cookers.config;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * Configuration class for setting up the Spring Mail with Gmail.
 * Reads the email credentials from Docker secrets and initializes the JavaMailSender.
 * <p>
 * Annotations:
 * - @Slf4j: Lombok annotation to generate a logger field.
 * - @NoArgsConstructor: Generates a no-arguments constructor.
 * - @Setter: Generates setter methods for all fields.
 * - @Getter: Generates getter methods for all fields.
 * - @Configuration: Indicates that this class contains Spring configuration.
 * <p>
 * Fields:
 * - springMailUser: The email user for sending mails.
 * - gMailAppPass: The application password for the Gmail account.
 * <p>
 * Methods:
 * - init(): Initializes the email credentials by reading Docker secrets.
 * - getJavaMailSender(): Configures and returns a JavaMailSender instance.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@Slf4j
@NoArgsConstructor
@Setter
@Getter
@Configuration
public class SpringMailConfig {

    private String springMailUser;
    @Getter(AccessLevel.NONE)
    private String gMailAppPass;

    /**
     * Initializes the email credentials by reading Docker secrets.
     * Sets the system properties for email user and password.
     */
    @PostConstruct
    public void init() {
        try {
            String USER = "SPRING_MAIL_VALIDATION";
            String APP_PASS = "GMAIL_APP_PASSWORD";

            springMailUser = DockerConfig.readSecret(USER);
            gMailAppPass = DockerConfig.readSecret(APP_PASS);

            if (StringUtils.hasText(springMailUser))
                System.setProperty(USER, springMailUser.trim());
            log.warn("springMailUser : {}", StringUtils.hasText(springMailUser));

            if (StringUtils.hasText(gMailAppPass))
                System.setProperty(APP_PASS, gMailAppPass.trim());
            log.warn("springMailPass : {}", StringUtils.hasText(gMailAppPass));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Configures and returns a JavaMailSender instance.
     *
     * @return The configured JavaMailSender instance.
     */
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(springMailUser);
        mailSender.setPassword(gMailAppPass);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
//        props.put("mail.debug", "true");

        return mailSender;
    }
}
