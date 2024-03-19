package com.whatacook.cookers.controler;

import com.whatacook.cookers.model.auth.ActivationDto;
import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.constants.Role;
import com.whatacook.cookers.model.users.UserDTO;
import io.netty.handler.codec.EncoderException;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class RegisterTest extends BaseTestClass {

    @Value("${security.jwt.sign-in-url}")
    private String usersRegisterEndpoint;
    private ArgumentCaptor<ActivationDto> activationCaptor;
    private ArgumentCaptor<MimeMessage> mimeMessageCaptor;


    @BeforeEach
    void setUp() {
        activationCaptor = ArgumentCaptor.forClass(ActivationDto.class);
        mimeMessageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        Mockito.when(userDao.existsByEmail(Mockito.anyString())).thenReturn(Mono.just(false));
        Mockito.when(userDao.save(Mockito.any(UserDTO.class))).thenReturn(Mono.just(userDtoBasicPending()));
        Mockito.when(activationDao.save(Mockito.any(ActivationDto.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        Mockito.when(emailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    }

    @Test
    void testRegisterIsCreated() {
        webTestClient.post().uri(usersRegisterEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBodyFullyFill())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo("User successfully created")
                .jsonPath("$.content.email").isEqualTo(EMAIL)
                .jsonPath("$.content.firstName").isEqualTo(FIRST_NAME)
                .jsonPath("$.content.surNames").isEqualTo(SUR_NAMES)
                .jsonPath("$.content.birthdate").isEqualTo(BIRTHDATE_STR)
                .jsonPath("$.content.roleType").isEqualTo(Role.BASIC.get())
                .jsonPath("$.content.accountStatus").isEqualTo(AccountStatus.PENDING.toString())
                .jsonPath("$.content.accountStatusMsg").isEqualTo(AccountStatus.PENDING.getDetails());

        Mockito.verify(activationDao).save(activationCaptor.capture());
        ActivationDto capturedActivation = activationCaptor.getValue();
        assertAll(
                () -> assertNotNull(capturedActivation.getCode()),
                () -> assertNotNull(capturedActivation.getExpiration()),
                () -> assertEquals(capturedActivation.getId(), ID)
        );

        Mockito.verify(emailSender, Mockito.times(1)).send(mimeMessageCaptor.capture());
        MimeMessage mimeMessage = mimeMessageCaptor.getValue();
        assertAll(
                () -> assertEquals(EMAIL, mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString()),
                () -> assertEquals("WhataCook : Activación de cuenta", mimeMessage.getSubject()),
                () -> assertEquals(springMailConfig.getSpringMailUser(), mimeMessage.getFrom()[0].toString()),
                () -> assertInstanceOf(MimeMultipart.class, mimeMessage.getContent()),
                () -> assertHtmlContentContains(mimeMessage, globalValues.getUrlActivationAccount() + capturedActivation.getCode())
        );

    }

    private void assertHtmlContentContains(MimeMessage mimeMessage, String activationURL) throws MessagingException, IOException, EncoderException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        mimeMessage.writeTo(output);
        final String inlineHtml = output.toString(StandardCharsets.UTF_8).replace("\n", empty)
                .replace("\r", empty).replace("=3D", empty);
        assertNotNull(inlineHtml, "No se encontró contenido HTML en el mensaje.");
        assertTrue(inlineHtml.contains(globalValues.getUrlWacLogoPngSmall()), "El mensaje no contiene el logo esperado.");
        assertTrue(inlineHtml.contains("Bienvenido a WhataCook, " + FIRST_NAME + "!"), "El mensaje no contiene el saludo esperado.");
        assertTrue(inlineHtml.contains(activationURL), "El mensaje no contiene el CODIGO esperado.");
    }

    @ParameterizedTest
    @MethodSource("provideVariablesForFailRequests")
    void testRegisterIsBadRequest(String requestBody, boolean success, String message, String key) {
        testFailRequest_400(usersRegisterEndpoint, requestBody, success, message, key);
    }

    private static Stream<Arguments> provideVariablesForFailRequests() {
        final String bodyWithoutEmail = requestBodyFullWithoutID(empty, PASSWORD, FIRST_NAME, SUR_NAMES, BIRTHDATE_STR);
        final String bodyWithoutPass = requestBodyFullWithoutID(EMAIL, empty, FIRST_NAME, SUR_NAMES, BIRTHDATE_STR);
        final String bodyWithoutFname = requestBodyFullWithoutID(EMAIL, PASSWORD, empty, SUR_NAMES, BIRTHDATE_STR);
        final String bodyWithoutSname = requestBodyFullWithoutID(EMAIL, PASSWORD, FIRST_NAME, empty, BIRTHDATE_STR);
        final String bodyWithoutBdate = requestBodyFullWithoutID(EMAIL, PASSWORD, FIRST_NAME, SUR_NAMES, empty);
        final String bodyWithEmailBlanc = requestBodyFullWithoutID(blank, PASSWORD, FIRST_NAME, SUR_NAMES, BIRTHDATE_STR);
        final String bodyWithPassBlanc = requestBodyFullWithoutID(EMAIL, blank, FIRST_NAME, SUR_NAMES, BIRTHDATE_STR);
        final String bodyWithFnameBlanc = requestBodyFullWithoutID(EMAIL, PASSWORD, blank, SUR_NAMES, BIRTHDATE_STR);
        final String bodyWithSnameBlanc = requestBodyFullWithoutID(EMAIL, PASSWORD, FIRST_NAME, blank, BIRTHDATE_STR);
        final String bodyWithBdateBlanc = requestBodyFullWithoutID(EMAIL, PASSWORD, FIRST_NAME, SUR_NAMES, blank);
        final String reqBodyPassWithoutLowercaseLetter = registerReqBoRdyNewPass("1234!ABC");
        final String reqBodyPassWithoutUppercaseLetter = registerReqBoRdyNewPass("1234!abc");
        final String reqBodyPassWithoutDigit = registerReqBoRdyNewPass("Abcd!efg");
        final String reqBodyPassWithoutSpecialChar = registerReqBoRdyNewPass("Abcd1Efg");
        final String reqBodyPassShorterThan8Char = registerReqBoRdyNewPass("A1b!ef");
        final String messageError = "Invalid or incorrect format";
        return Stream.of(
                Arguments.of(bodyWithoutEmail, false, messageError, "email"),
                Arguments.of(bodyWithoutPass, false, messageError, "password"),
                Arguments.of(bodyWithoutFname, false, messageError, "firstName"),
                Arguments.of(bodyWithoutSname, false, messageError, "surNames"),
                Arguments.of(bodyWithoutBdate, false, messageError, "birthdate"),
                Arguments.of(bodyWithEmailBlanc, false, messageError, "email"),
                Arguments.of(bodyWithPassBlanc, false, messageError, "password"),
                Arguments.of(bodyWithFnameBlanc, false, messageError, "firstName"),
                Arguments.of(bodyWithSnameBlanc, false, messageError, "surNames"),
                Arguments.of(bodyWithBdateBlanc, false, messageError, "birthdate"),
                Arguments.of(reqBodyPassWithoutLowercaseLetter, false, messageError, "password"),
                Arguments.of(reqBodyPassWithoutUppercaseLetter, false, messageError, "password"),
                Arguments.of(reqBodyPassWithoutDigit, false, messageError, "password"),
                Arguments.of(reqBodyPassWithoutSpecialChar, false, messageError, "password"),
                Arguments.of(reqBodyPassShorterThan8Char, false, messageError, "password"),
                Arguments.of(empty, false, "Invalid request body or not present", "ERROR")
        );
    }

    private static String registerReqBoRdyNewPass(String newPassword) {
        return requestBodyFullWithoutID(EMAIL, newPassword, FIRST_NAME, SUR_NAMES, BIRTHDATE_STR);
    }

}
