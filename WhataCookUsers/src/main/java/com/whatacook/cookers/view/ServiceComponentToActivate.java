package com.whatacook.cookers.view;

import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.model.users.UserJson;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class ServiceComponentToActivate {

    private final ActivationService activationService;
    private final EmailService emailService;
    private final UserDAO DAO;

    public ServiceComponentToActivate(ActivationService activationService, EmailService emailService, UserDAO DAO) {
        this.activationService = activationService;
        this.emailService = emailService;
        this.DAO = DAO;
    }

    public Mono<String> byActivationCodeSentByEmail(String activationCode) {
        return Mono.just(activationCode)
                .flatMap(activationService::findByCode)
                    .switchIfEmpty(Mono.error(UserServiceException.pull("This Code is Invalid")))
                .flatMap(activationDto -> {
                    if (ChronoUnit.HOURS.between(activationDto.getExpiration(), LocalDateTime.now()) > 24)
                        return Mono.error(UserServiceException.pull("This Code is Expired"));
                    else
                        return Mono.just(activationDto);
                })
                .flatMap(activationDto ->
                        DAO.findById(activationDto.getId())
                                .flatMap(userDTO -> {
                                    if (userDTO.getAccountStatus() == AccountStatus.PENDING) {
                                        userDTO.setAccountStatus(AccountStatus.OK);
                                        return DAO.save(userDTO)
                                                .then(activationService.deleteById(activationDto.getId()))
                                                .thenReturn(userDTO);
                                    } else {
                                        return Mono.error(UserServiceException.pull("The Account Status is not correct to activate account"));
                                    }
                                }))
                    .map(this::buildHtmlOkAccountActivatedContent)
                .onErrorMap(throwable -> UserServiceException.pull(throwable.getMessage()));
    }

    private String buildHtmlOkAccountActivatedContent(UserDTO userDTO) {
        String imageUrl = "https://i.imgur.com/gJaFpOa.png";
        String html = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <title>Cuenta Activada</title>
            </head>
            <body style="text-align: center; font-family: Arial, sans-serif; color: #333;">
                <div style="max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;">
                    <img src="%s" alt="Logo WhataCook" style="width: 200px; height: auto; margin-bottom: 20px;"/>
                        <h1 style="color: #4F81BD;">¡Hola, %s!</h1>
                            <h3>Su cuenta ha sido activada exitosamente.</h3>
                                <p>Puede volver a la aplicación y continuar con el inicio de sesión.</p>
                </div>
            </body>
            </html>
            """;
        return String.format(html, imageUrl, userDTO.getFirstName());
    }


    public Mono<UserJson> resendActivationCode(String email) {
        return DAO.findByEmail(email)
                    .switchIfEmpty(Mono.error(UserServiceException.pull("This Email is Invalid")))
                .flatMap(userDTO -> activationService.findById(userDTO.get_id())
                        .flatMap(activationDto -> {
                            if (ChronoUnit.HOURS.between(activationDto.getExpiration(), LocalDateTime.now()) <= 24) {
                                return emailService.sendActivationMail(activationDto, userDTO);
                            } else {
                                return emailService.createActivationCodeAndSendEmail(userDTO);
                            }
                        }))
                .onErrorMap(throwable -> UserServiceException.pull(throwable.getMessage()));
    }

}
