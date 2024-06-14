package com.whatacook.cookers.service;

import com.whatacook.cookers.config.SpringMailConfig;
import com.whatacook.cookers.model.auth.ActivationDto;
import com.whatacook.cookers.model.auth.ResetDto;
import com.whatacook.cookers.model.constants.Htmls;
import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDto;
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

    public Mono<UserJson> createActivationCodeAndSendEmail(UserDto userDTO) {
        return activationService.createNew(userDTO)
                .flatMap(activation -> sendActivationMail(activation, userDTO));
    }

    public Mono<UserJson> sendActivationMail(ActivationDto activationDto, UserDto userDTO) {
        return Mono.fromCallable(() -> buildMimeMessage(activationDto, userDTO))
                .flatMap(this::sendEmail)
                    .retry(2)
                .thenReturn(userDTO.toJsonWithoutId())
                .doOnError(UserServiceException::doOnErrorMap);
    }

    private MimeMessage buildMimeMessage(ActivationDto activationDto, UserDto userDTO) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(springMailConfig.getSpringMailUser());
        helper.setTo(userDTO.getEmail());
        helper.setSubject("WhataCook : Activación de cuenta");
        String content = buildHtmlContentToActivateAccount(activationDto, userDTO);
        helper.setText(content, true);
        return message;
    }

    private String buildHtmlContentToActivateAccount(ActivationDto activationDto, UserDto userDTO) {
        String activationLink = globalValues.getUrlActivationAccount() + activationDto.getCode();
        return Htmls.ActivationEmail.get()
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("USER_NAME", userDTO.getFirstName())
                .replace("ACTIVATION_LINK", activationLink);
    }

    private Mono<Void> sendEmail(MimeMessage message) {
        return Mono.fromRunnable(() -> emailSender.send(message));
    }

    public Mono<UserJson> createResetCodeAndSendEmail(UserDto userDTO) {
        return resetService.createNew(userDTO)
                .flatMap(resetCode -> sendResetMail(resetCode, userDTO));
    }

    public Mono<UserJson> sendResetMail(ResetDto resetCode, UserDto userDTO) {
        return Mono.fromCallable(() -> buildMimeMessage(resetCode, userDTO))
                .flatMap(this::sendEmail)
                    .retry(2)
                .thenReturn(userDTO.toJsonWithoutId())
                .doOnError(UserServiceException::doOnErrorMap);
    }

    private MimeMessage buildMimeMessage(ResetDto resetCode, UserDto userDTO) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(springMailConfig.getSpringMailUser());
        helper.setTo(userDTO.getEmail());
        helper.setSubject("WhataCook : reset contraseña");
        String content = buildHtmlContentToResetAccount(resetCode, userDTO);
        helper.setText(content, true);
        return message;
    }

    private String buildHtmlContentToResetAccount(ResetDto resetCode, UserDto userDTO) {
        String activationLink = globalValues.getUrlResetPassword() + resetCode.getCode();
        return Htmls.ResetPasswordMail.get()
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("USER_NAME", userDTO.getFirstName())
                .replace("ACTIVATION_LINK", activationLink);
    }
}