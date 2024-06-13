package com.whatacook.cookers.controller;

import com.whatacook.cookers.model.auth.ResetDto;
import com.whatacook.cookers.model.constants.Role;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.whatacook.cookers.model.constants.AccountStatus.OK;
import static org.junit.jupiter.api.Assertions.*;

public class ForgotPasswordTest extends BaseTestClass {

    @Value("${security.jwt.forgot-pass}")
    private String forgotPasswordEndpoint;

    private static final String requestBody = "{" +
            "\"email\": \"" + EMAIL + "\"," +
            "\"birthdate\": \"" + BIRTHDATE_STR + "\"" +
            "}";

    @BeforeEach
    void setUp() {
        Mockito.when(userDao.findByEmail(Mockito.anyString())).thenReturn(Mono.just(userDtoBasicOk()));
    }

    @Test
    void testForgotPasswordFailByIncorrectInformation() {
        webTestClient.post().uri(forgotPasswordEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody.replace(BIRTHDATE_STR, "1985-07-19"))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message").isEqualTo("Incorrect information");
    }

    @Test
    void testForgotPasswordFailByUnregisteredEmail() {
        Mockito.when(userDao.findByEmail(Mockito.anyString())).thenReturn(Mono.empty());
        webTestClient.post().uri(forgotPasswordEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody.replace(EMAIL, "bad@email.com"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message").isEqualTo("Unregistered email");
    }

    @Test
    void testForgotPasswordOk() {
        Mockito.when(resetDao.save(Mockito.any(ResetDto.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        Mockito.when(emailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
        mimeMessageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        resetCaptor = ArgumentCaptor.forClass(ResetDto.class);

        webTestClient.post().uri(forgotPasswordEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo("Email sent with reset code")
                .jsonPath("$.content.registration").value(registration ->
                        Assertions.assertThat(registration).asString().contains(LOCAL_DATE_TIME.toString()))
                .jsonPath("$.content.email").isEqualTo(EMAIL)
                .jsonPath("$.content.firstName").isEqualTo(FIRST_NAME)
                .jsonPath("$.content.surNames").isEqualTo(SUR_NAMES)
                .jsonPath("$.content.birthdate").isEqualTo(BIRTHDATE_STR)
                .jsonPath("$.content.roleType").isEqualTo(Role.BASIC.get())
                .jsonPath("$.content.accountStatus").isEqualTo(OK.toString())
                .jsonPath("$.content.accountStatusMsg").isEqualTo(OK.getDetails());

        Mockito.verify(resetDao).save(resetCaptor.capture());
        ResetDto capturedReset = resetCaptor.getValue();
        assertAll(
                () -> assertNotNull(capturedReset.getCode()),
                () -> assertTrue(ChronoUnit.HOURS.between(capturedReset.getExpiration(), LocalDateTime.now()) <= 24),
                () -> assertEquals(capturedReset.getId(), ID)
        );

        Mockito.verify(emailSender, Mockito.times(1)).send(mimeMessageCaptor.capture());
        MimeMessage mimeMessage = mimeMessageCaptor.getValue();
        String bodyMail = decodeMimeMsgFromQuotedPrintable(mimeMessage);
        assertAll(
                () -> assertEquals(EMAIL, mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString()),
                () -> assertEquals("WhataCook : reset contraseña", mimeMessage.getSubject()),
                () -> assertEquals(springMailConfig.getSpringMailUser(), mimeMessage.getFrom()[0].toString()),
                () -> assertInstanceOf(MimeMultipart.class, mimeMessage.getContent()),
                () -> assertNotNull(bodyMail, "No se encontró contenido HTML en el mensaje."),
                () -> assertTrue(bodyMail.contains(globalValues.getUrlWacLogoPngSmall()), "El mensaje no contiene el logo esperado."),
                () -> assertTrue(bodyMail.contains(", " + FIRST_NAME + "!"), "El mensaje no contiene el saludo esperado."),
                () -> assertTrue(bodyMail.contains(globalValues.getUrlResetPassword() + capturedReset.getCode()), "El mensaje no contiene el CODIGO esperado.")
        );
    }

}
