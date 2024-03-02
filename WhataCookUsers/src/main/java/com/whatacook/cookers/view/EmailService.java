package com.whatacook.cookers.view;

import com.whatacook.cookers.config.SpringMailConfig;
import com.whatacook.cookers.model.auth.ActivationDto;
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
    private final SpringMailConfig springMailConfig;
    private final ActivationService activationService;

    public EmailService(JavaMailSender emailSender, SpringMailConfig springMailConfig, ActivationService activationService) {
        this.emailSender = emailSender;
        this.springMailConfig = springMailConfig;
        this.activationService = activationService;
    }

    public Mono<UserJson> createActivationCodeAndSendEmail(UserDTO userDTO) {
        return activationService.createNew(userDTO)
                .flatMap(activation -> sendActivationMail(activation, userDTO));
    }

    public Mono<UserJson> sendActivationMail(ActivationDto activationDto, UserDTO userDTO) {
        return Mono.just(activationDto)
                .flatMap(activation -> {
                    SimpleMailMessage message = new SimpleMailMessage();
                    message.setFrom(springMailConfig.getSpringMailUser());
                    message.setTo(userDTO.getEmail());
                    message.setSubject("WhataCook : Activación de cuenta");
                    message.setText("Para activar su cuenta, por favor haga clic en el siguiente enlace: "
                            + "http://localhost:8080/api/users/activate?activationCode=" + activation.getCode());

                    return sendEmail(message)
                            .retry(2)
                            .then(Mono.just(userDTO.toJson()));
                })
                .doOnError(throwable -> UserServiceException.throwUp(throwable.getMessage()));
    }

    private Mono<Void> sendEmail(SimpleMailMessage message) {
        return Mono.fromRunnable(() -> emailSender.send(message));
    }

}