package com.whatacook.cookers.view;

import com.whatacook.cookers.config.SpringMailConfig;
import com.whatacook.cookers.model.auth.ActivationDto;
import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.model.users.UserJson;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
        return Mono.fromCallable(() -> buildMimeMessage(activationDto, userDTO))
                .flatMap(this::sendEmail)
                    .retry(2)
                .thenReturn(userDTO.toJson())
                .doOnError(throwable -> UserServiceException.throwUp(throwable.getMessage()));
    }


    private Mono<Void> sendEmail(SimpleMailMessage message) {
        return Mono.fromRunnable(() -> emailSender.send(message));
    }
    private Mono<Void> sendEmail(MimeMessage message) {
        return Mono.fromRunnable(() -> emailSender.send(message));
    }

    private SimpleMailMessage buildSimpleMailMessage(ActivationDto activationDto, UserDTO userDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(springMailConfig.getSpringMailUser());
        message.setTo(userDTO.getEmail());
        message.setSubject("WhataCook : Activación de cuenta");
        message.setText("Para activar su cuenta, por favor haga clic en el siguiente enlace: "
                + "http://localhost:8080/api/users/activate?activationCode=" + activationDto.getCode());
        return message;
    }

    private MimeMessage buildMimeMessage(ActivationDto activationDto, UserDTO userDTO) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(springMailConfig.getSpringMailUser());
        helper.setTo(userDTO.getEmail());
        helper.setSubject("WhataCook : Activación de cuenta");

        String content;

//        DEJO PARA DESPUÉS TESTAR EN EL .JAR VER SI FUCNIONA
//        Resource imageResource = new ClassPathResource("logo/logoWhataCook.png");
//        if (imageResource.exists()) {
//                helper.addInline("logo", imageResource);
//                content = buildHtmlContentWithImageCid(activationDto, userDTO);
//        } else
            content = buildHtmlContent(activationDto, userDTO);



        helper.setText(content, true);
        return message;
    }

    private String buildHtmlContent(ActivationDto activationDto, UserDTO userDTO) {
        String imageUrl = "https://i.imgur.com/gJaFpOa.png";
        String activationLink = "http://localhost:8080/api/users/activate?activationCode=" + activationDto.getCode();
        return "<!DOCTYPE html>"
                + "<html lang='es'>"
                + "<head>"
                + "<meta charset='UTF-8'>"
                + "<title>Activación de Cuenta</title>"
                + "</head>"
                + "<body>"
                + "<img src=\"" + imageUrl + "\" alt='Logo WhataCook'>"
                + "<h1>Bienvenido a WhataCook, " + userDTO.getFirstName() + "!</h1>"
                + "<p>Para activar su cuenta, por favor haga clic en el siguiente enlace:</p>"
                + "<a href='" + activationLink + "'>Activar Cuenta</a>"
                + "</body>"
                + "</html>";
    }

    private String buildHtmlContentWithImageCid(ActivationDto activationDto, UserDTO userDTO) {
        String activationLink = "http://localhost:8080/api/users/activate?activationCode=" + activationDto.getCode();

        return "<!DOCTYPE html>"
                + "<html lang='es'>"
                + "<head>"
                + "<meta charset='UTF-8'>"
                + "<title>Activación de Cuenta</title>"
                + "</head>"
                + "<body>"
                + "<img src=\"cid:logo\" alt='Logo WhataCook'>"
                + "<h1>Bienvenido a WhataCook, " + userDTO.getFirstName() + "!</h1>"
                + "<p>Para activar su cuenta, por favor haga clic en el siguiente enlace:</p>"
                + "<a href='" + activationLink + "'>Activar Cuenta</a>"
                + "</body>"
                + "</html>";
    }


}