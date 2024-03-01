package com.whatacook.cookers.view;

import com.whatacook.cookers.config.SpringMailConfig;
import com.whatacook.cookers.config.jwt.JwtUtil;
import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.model.users.UserJson;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class EmailService {

    private final JavaMailSender emailSender;
    private final JwtUtil jwtUtil;
    private final SpringMailConfig springMailConfig;

    public EmailService(JavaMailSender emailSender, JwtUtil jwtUtil, SpringMailConfig springMailConfig) {
        this.emailSender = emailSender;
        this.jwtUtil = jwtUtil;
        this.springMailConfig = springMailConfig;
    }

    public Mono<UserJson> sendActivationEmail(UserDTO userDTO) {
        return Mono.fromCallable(() -> {
                    String tokenActivation = jwtUtil.generateToken(userDTO.get_id());
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setFrom(springMailConfig.getSpringMailUser());
                    message.setTo(userDTO.getEmail());
                    message.setSubject("WhataCook : Activación de cuenta");
                    message.setText("Para activar su cuenta, por favor haga clic en el siguiente enlace: "
                            + "http://localhost:8080/api/users/activate?token=" + tokenActivation);
                    emailSender.send(message);
                    userDTO.set_id(null); //to avoid leakage of information that will allow account activation
                    return userDTO.toJson();
                })
                .retry(2) // Número de reintentos en caso de falla
                .doOnError(throwable -> UserServiceException.throwUp(throwable.getMessage()));

    }

}