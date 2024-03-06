package com.whatacook.cookers.config;

import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Properties;

@NoArgsConstructor
@Setter @Getter
@Configuration
public class SpringMailConfig {

    private String springMailUser;
    @Getter(AccessLevel.NONE)
    private String springMailPass;
    @Getter(AccessLevel.NONE)
    private String gMailAppPass;
    @PostConstruct
    public void init() {

        try {
            String USER = "SPRING_MAIL_VALIDATION";
            String APP_PASS = "GMAIL_APP_PASSWORD";

            springMailUser = DockerConfig.readSecret(USER);
            gMailAppPass = DockerConfig.readSecret(APP_PASS);

            if (StringUtils.hasText(springMailUser) && StringUtils.hasText(gMailAppPass)) {
                System.setProperty("SPRING_MAIL_VALIDATION", springMailUser.trim());
                System.setProperty("GMAIL_APP_PASSWORD", gMailAppPass.trim());
            }
        } catch (IOException e) {  throw new RuntimeException(e); }

    }

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
