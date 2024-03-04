package com.whatacook.cookers.service;

import com.whatacook.cookers.config.SpringMailConfig;
import com.whatacook.cookers.model.auth.ActivationDto;
import com.whatacook.cookers.model.constants.Htmls;
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

@AllArgsConstructor
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
                .doOnError(UserServiceException::doOnErrorMap);
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
        return String.format(Htmls.ActivationEmail.get(), imageUrl, userDTO.getFirstName(), activationLink, activationLink, activationLink);
    }


}