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

/**
 * Service class for handling email-related operations.
 * <p>
 * Annotations:
 * - @AllArgsConstructor: Generates a constructor with 1 parameter for each field.
 * - @Component: Indicates that this class is a Spring component.
 * <p>
 * Fields:
 * - emailSender: The JavaMailSender instance for sending emails.
 * - springMailConfig: Configuration class for Spring Mail.
 * - activationService: Service for handling activation operations.
 * - resetService: Service for handling password reset operations.
 * - globalValues: Global values used in the application.
 * <p>
 * Methods:
 * - createActivationCodeAndSendEmail(UserDto userDTO): Creates an activation code and sends an email.
 * - sendActivationMail(ActivationDto activationDto, UserDto userDTO): Sends an activation email.
 * - buildMimeMessage(ActivationDto activationDto, UserDto userDTO): Builds a MimeMessage for activation.
 * - buildHtmlContentToActivateAccount(ActivationDto activationDto, UserDto userDTO): Builds HTML content for the activation email.
 * - sendEmail(MimeMessage message): Sends an email.
 * - createResetCodeAndSendEmail(UserDto userDTO): Creates a reset code and sends an email.
 * - sendResetMail(ResetDto resetCode, UserDto userDTO): Sends a reset email.
 * - buildMimeMessage(ResetDto resetCode, UserDto userDTO): Builds a MimeMessage for password reset.
 * - buildHtmlContentToResetAccount(ResetDto resetCode, UserDto userDTO): Builds HTML content for the reset email.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see SpringMailConfig
 * @see ActivationService
 * @see ResetService
 * @see GlobalValues
 * @see JavaMailSender
 * @see MimeMessage
 * @see MimeMessageHelper
 * @see Htmls
 * @see UserDto
 * @see UserJson
 * @see Mono
 * @see MessagingException
 * @see AllArgsConstructor
 * @see Component
 */
@AllArgsConstructor
@Component
public class EmailService {

    private final JavaMailSender emailSender;
    private final SpringMailConfig springMailConfig;
    private final ActivationService activationService;
    private final ResetService resetService;
    private final GlobalValues globalValues;

    /**
     * Creates an activation code and sends an email.
     *
     * @param userDTO the user details to save
     * @return a Mono containing the user details without ID
     */
    public Mono<UserJson> createActivationCodeAndSendEmail(UserDto userDTO) {
        return activationService.createNew(userDTO)
                .flatMap(activation -> sendActivationMail(activation, userDTO));
    }

    /**
     * Sends an activation email.
     *
     * @param activationDto the activation details
     * @param userDTO       the user details
     * @return a Mono containing the user details without ID
     */
    public Mono<UserJson> sendActivationMail(ActivationDto activationDto, UserDto userDTO) {
        return Mono.fromCallable(() -> buildMimeMessage(activationDto, userDTO))
                .flatMap(this::sendEmail)
                .retry(2)
                .thenReturn(userDTO.toJsonWithoutId())
                .doOnError(UserServiceException::doOnErrorMap);
    }

    /**
     * Builds a MimeMessage for activation.
     *
     * @param activationDto the activation details
     * @param userDTO       the user details
     * @return the built MimeMessage
     * @throws MessagingException if an error occurs while building the message
     */
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

    /**
     * Builds HTML content for the activation email.
     *
     * @param activationDto the activation details
     * @param userDTO       the user details
     * @return the HTML content
     */
    private String buildHtmlContentToActivateAccount(ActivationDto activationDto, UserDto userDTO) {
        String activationLink = globalValues.getUrlActivationAccount() + activationDto.getCode();
        return Htmls.ActivationEmail.get()
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("USER_NAME", userDTO.getFirstName())
                .replace("ACTIVATION_LINK", activationLink);
    }

    /**
     * Sends an email.
     *
     * @param message the MimeMessage to send
     * @return a Mono signaling completion
     */
    private Mono<Void> sendEmail(MimeMessage message) {
        return Mono.fromRunnable(() -> emailSender.send(message));
    }

    /**
     * Creates a reset code and sends an email.
     *
     * @param userDTO the user details to save
     * @return a Mono containing the user details without ID
     */
    public Mono<UserJson> createResetCodeAndSendEmail(UserDto userDTO) {
        return resetService.createNew(userDTO)
                .flatMap(resetCode -> sendResetMail(resetCode, userDTO));
    }

    /**
     * Sends a reset email.
     *
     * @param resetCode the reset details
     * @param userDTO   the user details
     * @return a Mono containing the user details without ID
     */
    public Mono<UserJson> sendResetMail(ResetDto resetCode, UserDto userDTO) {
        return Mono.fromCallable(() -> buildMimeMessage(resetCode, userDTO))
                .flatMap(this::sendEmail)
                .retry(2)
                .thenReturn(userDTO.toJsonWithoutId())
                .doOnError(UserServiceException::doOnErrorMap);
    }

    /**
     * Builds a MimeMessage for password reset.
     *
     * @param resetCode the reset details
     * @param userDTO   the user details
     * @return the built MimeMessage
     * @throws MessagingException if an error occurs while building the message
     */
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

    /**
     * Builds HTML content for the reset email.
     *
     * @param resetCode the reset details
     * @param userDTO   the user details
     * @return the HTML content
     */
    private String buildHtmlContentToResetAccount(ResetDto resetCode, UserDto userDTO) {
        String activationLink = globalValues.getUrlResetPassword() + resetCode.getCode();
        return Htmls.ResetPasswordMail.get()
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("USER_NAME", userDTO.getFirstName())
                .replace("ACTIVATION_LINK", activationLink);
    }
}
