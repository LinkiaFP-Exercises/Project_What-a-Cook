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
        String inlineHtml = output.toString(StandardCharsets.UTF_8).replace("\n", "")
                                    .replace("\r", "").replace("=3D", "");
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
        String bodyWithoutEmail = requestBodyFull("", PASSWORD, FIRST_NAME, SUR_NAMES, BIRTHDATE_STR);
        String bodyWithoutPass = requestBodyFull(EMAIL, "", FIRST_NAME, SUR_NAMES, BIRTHDATE_STR);
        String bodyWithoutFname = requestBodyFull(EMAIL, PASSWORD, "", SUR_NAMES, BIRTHDATE_STR);
        String bodyWithoutSname = requestBodyFull(EMAIL, PASSWORD, FIRST_NAME, "", BIRTHDATE_STR);
        String bodyWithoutBdate = requestBodyFull(EMAIL, PASSWORD, FIRST_NAME, SUR_NAMES, "");
        String bodyWithEmailBlanc = requestBodyFull("     ", PASSWORD, FIRST_NAME, SUR_NAMES, BIRTHDATE_STR);
        String bodyWithPassBlanc = requestBodyFull(EMAIL, "     ", FIRST_NAME, SUR_NAMES, BIRTHDATE_STR);
        String bodyWithFnameBlanc = requestBodyFull(EMAIL, PASSWORD, "    ", SUR_NAMES, BIRTHDATE_STR);
        String bodyWithSnameBlanc = requestBodyFull(EMAIL, PASSWORD, FIRST_NAME, "    ", BIRTHDATE_STR);
        String bodyWithBdateBlanc = requestBodyFull(EMAIL, PASSWORD, FIRST_NAME, SUR_NAMES, "     ");
        String withoutLowercaseLetter = requestBodyFull(EMAIL, "1234!ABC", FIRST_NAME, SUR_NAMES, BIRTHDATE_STR);
        String withoutUppercaseLetter = requestBodyFull(EMAIL, "1234!abc", FIRST_NAME, SUR_NAMES, BIRTHDATE_STR);
        String withoutDigit = requestBodyFull(EMAIL, "Abcd!efg", FIRST_NAME, SUR_NAMES, BIRTHDATE_STR);
        String withoutSpecialChar = requestBodyFull(EMAIL, "Abcd1Efg", FIRST_NAME, SUR_NAMES, BIRTHDATE_STR);
        String shorterThan8Char = requestBodyFull(EMAIL, "A1b!ef", FIRST_NAME, SUR_NAMES, BIRTHDATE_STR);
        String messageError = "Invalid or incorrect format";
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
                Arguments.of(withoutLowercaseLetter, false, messageError, "password"),
                Arguments.of(withoutUppercaseLetter, false, messageError, "password"),
                Arguments.of(withoutDigit, false, messageError, "password"),
                Arguments.of(withoutSpecialChar, false, messageError, "password"),
                Arguments.of(shorterThan8Char, false, messageError, "password"),
                Arguments.of("", false, "Invalid request body or not present", "ERROR")
        );
    }

}
