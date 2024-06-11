package com.whatacook.cookers.controler;

import com.whatacook.cookers.TestMongoConfig;
import com.whatacook.cookers.WhataCookUsersApplication;
import com.whatacook.cookers.config.SpringMailConfig;
import com.whatacook.cookers.config.jwt.JwtUtil;
import com.whatacook.cookers.model.auth.ActivationDto;
import com.whatacook.cookers.model.auth.ResetDto;
import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.constants.Role;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.service.contracts.ActivationDao;
import com.whatacook.cookers.service.contracts.ResetDao;
import com.whatacook.cookers.service.contracts.UserDao;
import com.whatacook.cookers.utilities.GlobalValues;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.MimeUtility;
import org.apache.commons.io.IOUtils;
import org.assertj.core.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {WhataCookUsersApplication.class, TestMongoConfig.class})
@ActiveProfiles("test")
public class BaseTestClass {

    private static final Logger log = LoggerFactory.getLogger(BaseTestClass.class);
    @Autowired
    protected WebTestClient webTestClient;
    @Autowired
    protected SpringMailConfig springMailConfig;
    @Autowired
    protected GlobalValues globalValues;
    @Autowired
    protected JwtUtil jwtUtil;

    @MockBean
    protected UserDao userDao;
    @MockBean
    protected ActivationDao activationDao;
    @MockBean
    protected ResetDao resetDao;
    @MockBean
    protected JavaMailSender emailSender;

    protected static final String empty = "";
    protected static final String blank = "    ";
    ArgumentCaptor<ResetDto> resetCaptor;
    ArgumentCaptor<ActivationDto> activationCaptor;
    ArgumentCaptor<MimeMessage> mimeMessageCaptor;

    protected static String requestBodyOnlyMail(String email) {
        return "{ \"email\": \"" + email + "\" }";
    }

    protected static String requestBodyFullWithoutID(String email, String password, String firstName, String surNames, String birthdate) {
        return "{\n" +
                "    \"email\": \"" + email + "\",\n" +
                "    \"password\": \"" + password + "\",\n" +
                "    \"firstName\": \"" + firstName + "\",\n" +
                "    \"surNames\": \"" + surNames + "\",\n" +
                "    \"birthdate\": \"" + birthdate + "\"\n" +
                "}";
    }

    protected static String requestBodyFullWhitID(String _id, String email, String password, String firstName, String surNames, String birthdate) {
        return "{\n" +
                "    \"_id\": \"" + _id + "\",\n" +
                "    \"email\": \"" + email + "\",\n" +
                "    \"password\": \"" + password + "\",\n" +
                "    \"firstName\": \"" + firstName + "\",\n" +
                "    \"surNames\": \"" + surNames + "\",\n" +
                "    \"birthdate\": \"" + birthdate + "\"\n" +
                "}";
    }

    protected static String requestBodyFullWhitIdAndNewPassword(String _id, String email, String password, String newPassword, String firstName, String surNames, String birthdate) {
        return "{\n" +
                "    \"_id\": \"" + _id + "\",\n" +
                "    \"email\": \"" + email + "\",\n" +
                "    \"password\": \"" + password + "\",\n" +
                "    \"newPassword\": \"" + newPassword + "\",\n" +
                "    \"firstName\": \"" + firstName + "\",\n" +
                "    \"surNames\": \"" + surNames + "\",\n" +
                "    \"birthdate\": \"" + birthdate + "\"\n" +
                "}";
    }

    protected static String requestBodyFullyFill() {
        return requestBodyFullWithoutID(EMAIL, PASSWORD, FIRST_NAME, SUR_NAMES, BIRTHDATE_STR);
    }

    protected static UserDTO userDtoBasicPending() {
        UserDTO userDTO = new UserDTO();
        userDTO.set_id(ID);
        userDTO.setRegistration(LOCAL_DATE_TIME);
        userDTO.setEmail(EMAIL);
        userDTO.setPassword(PASSWORD_ENCRYPT);
        userDTO.setFirstName(FIRST_NAME);
        userDTO.setSurNames(SUR_NAMES);
        userDTO.setBirthdate(BIRTHDATE);
        userDTO.setRoleType(Role.BASIC);
        userDTO.setAccountStatus(AccountStatus.PENDING);
        return userDTO;
    }

    protected static UserDTO userDtoBasicAccountStatus(AccountStatus status) {
        UserDTO userDTO = userDtoBasicPending();
        userDTO.setAccountStatus(status);
        userDTO.setRequestDeleteDate(LocalDateTime.now().minusYears(3));
        return userDTO;
    }

    protected static UserDTO userDtoBasicOk() {
        UserDTO userDTO = userDtoBasicPending();
        userDTO.setAccountStatus(AccountStatus.OK);
        return userDTO;
    }

    protected static UserDTO userDtoAdminOk(String email) {
        UserDTO userDTO = userDtoBasicOk();
        userDTO.setRoleType(Role.FULL);
        userDTO.setEmail(email);
        return userDTO;
    }

    protected static UserDTO userDtoOtherOk(String email) {
        UserDTO userDTO = userDtoBasicOk();
        userDTO.setEmail(email);
        return userDTO;
    }

    protected String tokenUserOk() {
        return jwtUtil.getPrefix() + jwtUtil.doGenerateToken(tokenRole(Role.BASIC), EMAIL);
    }

    protected String tokenAdminOk(String email) {
        return jwtUtil.getPrefix() + jwtUtil.doGenerateToken(tokenRole(Role.CHIEF), email);
    }

    protected String tokenOtherUserOk(String email) {
        return jwtUtil.getPrefix() + jwtUtil.doGenerateToken(tokenRole(Role.BASIC), email);
    }

    protected String tokenExpired(String email) {
        return jwtUtil.getPrefix() + jwtUtil.generateExpiredTokenForTest(tokenRole(Role.BASIC), email);
    }

    protected HashMap<String, Object> tokenRole(Role role) {
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("roles", List.of("ROLE_" + role.get()));
        return claims;
    }

    public static final String ID = "65db4a16e6cd946d5eb775fa";
    public static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2024, 1, 20, 16, 20);
    public static final String EMAIL = "fulano@test.ch";
    public static final String PASSWORD = "Test!234";
    public static final String PASSWORD_ENCRYPT = "$2a$10$FlrzGLiGkTe7blCuE6ZyMOwJ8Ru/D6aAmlvuQgJvRqd/cpCJvQUWa";
    public static final String FIRST_NAME = "Fulano";
    public static final String SUR_NAMES = "Ciclano Beltrano";
    public static final LocalDate BIRTHDATE = LocalDate.of(1982, 7, 19);
    public static final String BIRTHDATE_STR = "1982-07-19";

    void testFailRequest_400(String uri, String requestBody, boolean success, String message, String key) {
        webTestClient.post().uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(success)
                .jsonPath("$.message").value(text -> Assertions.assertThat(text).asString().contains(message))
                .jsonPath("$.content").value(content -> assertThat(content).asInstanceOf(MAP).containsKeys(key));
    }

    void testPost401EndpointWithTokenSuccessFalseMessageContains(String endpoint, String token, String requestBody, String message) {
        webTestClient.post().uri(endpoint)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message").value(text -> Assertions.assertThat(text).asString().contains(message));
    }

    void testPutEndpointWithTokenSuccessFalseMessageContains(String endpoint, String token, String requestBody, int http, String message) {
        webTestClient.put().uri(endpoint)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isEqualTo(http)
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message").value(text -> Assertions.assertThat(text).asString().contains(message));
    }

    ActivationDto mokitoVerifyActvationDaoSaveAndAssertActivationCode(String oldActivationCode) {
        Mockito.verify(activationDao).save(activationCaptor.capture());
        ActivationDto capturedActivation = activationCaptor.getValue();
        assertAll(
                () -> assertNotNull(capturedActivation.getCode()),
                () -> assertFalse(capturedActivation.getCode().equals(oldActivationCode)),
                () -> assertTrue(ChronoUnit.HOURS.between(capturedActivation.getExpiration(), LocalDateTime.now()) <= 24),
                () -> assertEquals(capturedActivation.getId(), ID)
        );
        return capturedActivation;
    }

    void mokitoVerifyEmailSenderAndCompareActivationCodeAndmimeMsg(ActivationDto activationDto) {
        Mockito.verify(emailSender, Mockito.times(1)).send(mimeMessageCaptor.capture());
        MimeMessage mimeMessage = mimeMessageCaptor.getValue();
        String bodyMail = decodeMimeMsgFromQuotedPrintable(mimeMessage);
        assertAll(
                () -> assertEquals(EMAIL, mimeMessage.getRecipients(Message.RecipientType.TO)[0].toString()),
                () -> assertEquals("WhataCook : Activación de cuenta", mimeMessage.getSubject()),
                () -> assertEquals(springMailConfig.getSpringMailUser(), mimeMessage.getFrom()[0].toString()),
                () -> assertInstanceOf(MimeMultipart.class, mimeMessage.getContent()),
                () -> assertNotNull(bodyMail, "No se encontró contenido HTML en el mensaje."),
                () -> assertTrue(bodyMail.contains(globalValues.getUrlWacLogoPngSmall()), "El mensaje no contiene el logo esperado."),
                () -> assertTrue(bodyMail.contains(", " + FIRST_NAME + "!"), "El mensaje no contiene el saludo esperado."),
                () -> assertTrue(bodyMail.contains(globalValues.getUrlActivationAccount() + activationDto.getCode()), "El mensaje no contiene el CODIGO esperado.")
        );
    }

    String decodeMimeMsgFromQuotedPrintable(MimeMessage mimeMessage) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        StringWriter writer = new StringWriter();
        try {
            mimeMessage.writeTo(output);
            // Decodificar el contenido Quoted-Printable
            String encodedContent = output.toString(StandardCharsets.UTF_8);
            ByteArrayInputStream encodedStream = new ByteArrayInputStream(encodedContent.getBytes(StandardCharsets.UTF_8));
            InputStream decodedStream = MimeUtility.decode(encodedStream, "quoted-printable");
            // Convertir el contenido decodificado a una cadena
            IOUtils.copy(decodedStream, writer, StandardCharsets.UTF_8);
        } catch (IOException | MessagingException e) {
            log.warn(e.getMessage());
        }
        return writer.toString();
    }

}
