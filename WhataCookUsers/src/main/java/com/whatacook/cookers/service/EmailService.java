package com.whatacook.cookers.service;

import com.whatacook.cookers.config.SpringMailConfig;
import com.whatacook.cookers.model.auth.ActivationDto;
import com.whatacook.cookers.model.auth.ResetDto;
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
    private final ResetService resetService;
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

    private MimeMessage buildMimeMessage(ActivationDto activationDto, UserDTO userDTO) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(springMailConfig.getSpringMailUser());
        helper.setTo(userDTO.getEmail());
        helper.setSubject("WhataCook : Activación de cuenta");
        String content = buildHtmlContentToActivateAccount(activationDto, userDTO);
        helper.setText(content, true);
        return message;
    }

    private String buildHtmlContentToActivateAccount(ActivationDto activationDto, UserDTO userDTO) {
        String activationLink = globalValues.getUrlActivationAccount() + activationDto.getCode();
        return String.format(Htmls.ActivationEmail.get(), globalValues.getUrlWacLogoPngSmall(),
                        userDTO.getFirstName(), activationLink, activationLink, activationLink);
    }

    private Mono<Void> sendEmail(MimeMessage message) {
        return Mono.fromRunnable(() -> emailSender.send(message));
    }

    public Mono<UserJson> createResetCodeAndSendEmail(UserDTO userDTO) {
        return resetService.createNew(userDTO)
                .flatMap(resetCode -> sendResetMail(resetCode, userDTO));
    }

    public Mono<UserJson> sendResetMail(ResetDto resetCode, UserDTO userDTO) {
        return Mono.fromCallable(() -> buildMimeMessage(resetCode, userDTO))
                .flatMap(this::sendEmail)
                    .retry(2)
                .thenReturn(userDTO.toJson())
                .doOnError(UserServiceException::doOnErrorMap);
    }

    private MimeMessage buildMimeMessage(ResetDto resetCode, UserDTO userDTO) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(springMailConfig.getSpringMailUser());
        helper.setTo(userDTO.getEmail());
        helper.setSubject("WhataCook : reset contraseña");
        String content = buildHtmlContentToResetAccount(resetCode, userDTO);
        helper.setText(content, true);
        return message;
    }

    private String buildHtmlContentToResetAccount(ResetDto resetCode, UserDTO userDTO) {
        String activationLink = globalValues.getUrlResetPassword() + resetCode.getCode();
        return String.format(Htmls.ResetPasswordMail.get(), globalValues.getUrlWacLogoPngSmall(),
                userDTO.getFirstName(), activationLink, activationLink, activationLink);
    }
}