package com.whatacook.cookers.view;

import com.whatacook.cookers.config.SpringMailConfig;
import com.whatacook.cookers.model.auth.ActivationDto;
import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.utilities.GlobalValues;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class EmailService {

    private final JavaMailSender emailSender;
    private final SpringMailConfig springMailConfig;
    private final ActivationService activationService;
    private final GlobalValues globalValues;

    public Mono<UserJson> createActivationCodeAndSendEmail(UserDTO userDTO) {
        return activationService.createNew(userDTO)
                .flatMap(activation -> sendActivationMail(activation, userDTO));
    }

    public Mono<UserJson> sendActivationMail(ActivationDto activationDto, UserDTO userDTO) {
        return Mono.fromCallable(() -> buildMimeMessage(activationDto, userDTO))
                .flatMap(this::sendEmail)
                    .retry(2)
                .thenReturn(userDTO.toJson())
                .doOnError(throwable -> UserServiceException.throwUp(throwable.getMessage()));
    }

    private Mono<Void> sendEmail(MimeMessage message) {
        return Mono.fromRunnable(() -> emailSender.send(message));
    }

    private MimeMessage buildMimeMessage(ActivationDto activationDto, UserDTO userDTO) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(springMailConfig.getSpringMailUser());
        helper.setTo(userDTO.getEmail());
        helper.setSubject("WhataCook : Activación de cuenta");
        String content = buildHtmlContent(activationDto, userDTO);
        helper.setText(content, true);
        return message;
    }

    private String buildHtmlContent(ActivationDto activationDto, UserDTO userDTO) {
        String imageUrl = "https://i.imgur.com/gJaFpOa.png";
        String activationLink = "http://localhost:8080/api/users/activate?activationCode=" + activationDto.getCode();
        String html = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
                <meta charset="UTF-8">
                <title>Activación de Cuenta</title>
            </head>
            <body style="text-align: center; font-family: Arial, sans-serif; color: #333;">
                <div style="max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;">
                    <img src="%s" alt="Logo WhataCook" style="width: 100px; height: auto;"/>
                    <h1 style="color: #4F81BD;">Bienvenido a WhataCook, %s!</h1>
                    <p>Para activar su cuenta, por favor haga clic en el siguiente botón:</p>
                    <a href="%s" style="background-color: #4F81BD; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; display: inline-block;">Activar Cuenta</a>
                    <p style="font-size: 12px; margin-top: 15px;">Si no puede hacer clic en el botón, copie y pegue este enlace en su navegador:</p>
                    <p style="font-size: 12px;"><a href="%s">%s</a></p>
                </div>
            </body>
            </html>
            """;
        return String.format(html, imageUrl, userDTO.getFirstName(), activationLink, activationLink, activationLink);
    }


}