package com.whatacook.cookers.controller;

import com.whatacook.cookers.model.constants.Role;
import com.whatacook.cookers.model.users.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.stream.Stream;

import static com.whatacook.cookers.model.constants.AccountStatus.OK;
import static com.whatacook.cookers.utilities.Util.encryptMatches;
import static org.junit.jupiter.api.Assertions.*;

public class UpdateUserTest extends BaseTestClass {

    @Value("${app.endpoint.users}")
    private String updateOneEndpoint;

    private ArgumentCaptor<UserDTO> argumentCaptor;


    @BeforeEach
    void setUp() {
        argumentCaptor = ArgumentCaptor.forClass(UserDTO.class);
        Mockito.when(userDao.findByEmail(EMAIL)).thenReturn(Mono.just(userDtoBasicOk()));
        Mockito.when(userDao.findBy_id(ID)).thenReturn(Mono.just(userDtoBasicOk()));
        Mockito.when(userDao.save(Mockito.any(UserDTO.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArguments()[0]));
    }

    @ParameterizedTest
    @MethodSource("provideVariablesForSuccessUpdates")
    void testUpdateUserWithValidData(String requestBody, String email, String password, String firstName, String surNames, String birthdate) {
        final String message = "User successfully UPDATED";
        webTestClient.put().uri(updateOneEndpoint)
                .header("Authorization", tokenUserOk())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo(message)
                .jsonPath("$.content._id").isEqualTo(ID)
                .jsonPath("$.content.email").isEqualTo(email)
                .jsonPath("$.content.firstName").isEqualTo(firstName)
                .jsonPath("$.content.surNames").isEqualTo(surNames)
                .jsonPath("$.content.birthdate").isEqualTo(birthdate)
                .jsonPath("$.content.roleType").isEqualTo(Role.BASIC.get())
                .jsonPath("$.content.accountStatus").isEqualTo(OK.toString())
                .jsonPath("$.content.accountStatusMsg").isEqualTo(OK.getDetails());

        Mockito.verify(userDao).save(argumentCaptor.capture());
        UserDTO capturedUserDTO = argumentCaptor.getValue();
        final String[] dateParts = birthdate.split("-");
        final int year = Integer.parseInt(dateParts[0]);
        final int month = Integer.parseInt(dateParts[1]);
        final int day = Integer.parseInt(dateParts[2]);

        assertAll(
                () -> assertEquals(email, capturedUserDTO.getEmail()),
                () -> assertTrue(encryptMatches(password, capturedUserDTO.getPassword())),
                () -> assertEquals(firstName, capturedUserDTO.getFirstName()),
                () -> assertEquals(surNames, capturedUserDTO.getSurNames()),
                () -> assertEquals(LocalDate.of(year, month, day), capturedUserDTO.getBirthdate())
        );
    }

    private static Stream<Arguments> provideVariablesForSuccessUpdates() {
        String newEmail = "test@test.com";
        String newPass = "tesT321@";
        String newName = "Fauno";
        String newSurname = "Guazina";
        String newBirthdate = "1999-09-09";
        final String reqBodyJustEmail = updateReqBody(newEmail, empty, empty, empty, empty);
        final String reqBodyNewPass = updateReqBodyNewPass(newPass);
        final String reqBodyJustFname = updateReqBody(empty, empty, newName, empty, empty);
        final String reqBodyJustSname = updateReqBody(empty, empty, empty, newSurname, empty);
        final String reqBodyJustBdate = updateReqBody(empty, empty, empty, empty, newBirthdate);
        return Stream.of(
                Arguments.of(reqBodyJustEmail, newEmail, PASSWORD, FIRST_NAME, SUR_NAMES, BIRTHDATE_STR),
                Arguments.of(reqBodyNewPass, EMAIL, newPass, FIRST_NAME, SUR_NAMES, BIRTHDATE_STR),
                Arguments.of(reqBodyJustFname, EMAIL, PASSWORD, newName, SUR_NAMES, BIRTHDATE_STR),
                Arguments.of(reqBodyJustSname, EMAIL, PASSWORD, FIRST_NAME, newSurname, BIRTHDATE_STR),
                Arguments.of(reqBodyJustBdate, EMAIL, PASSWORD, FIRST_NAME, SUR_NAMES, newBirthdate)
        );
    }

    @ParameterizedTest
    @MethodSource("provideVariablesForFailUpdates")
    void testUpdateUserWithInvalidData(String requestBody, int httpStatus, String message) {
        testPutEndpointWithTokenSuccessFalseMessageContains(updateOneEndpoint, tokenUserOk(), requestBody, httpStatus, message);
    }

    private static Stream<Arguments> provideVariablesForFailUpdates() {
        final String notUpdateMsg = "No update required or data is invalid";
        final String unAuthMessage = "No tienes permiso para acceder a esta información";
        final String invalidBody = "Invalid request body or not present";
        final int httpOk = HttpStatus.OK.value();
        final int httpBad = HttpStatus.BAD_REQUEST.value();
        final int httpUnAuth = HttpStatus.UNAUTHORIZED.value();

        final String reqBodyPassWithoutLowercaseLetter = updateReqBodyNewPass("1234!ABC");
        final String reqBodyPassWithoutUppercaseLetter = updateReqBodyNewPass("1234!abc");
        final String reqBodyPassWithoutDigit = updateReqBodyNewPass("Abcd!efg");
        final String reqBodyPassWithoutSpecialChar = updateReqBodyNewPass("Abcd1Efg");
        final String reqBodyPassShorterThan8Char = updateReqBodyNewPass("A1b!ef");

        return Stream.of(
                Arguments.of(updateReqBody(empty, empty, empty, empty, empty), httpOk, notUpdateMsg),
                Arguments.of(updateReqBody(blank, blank, blank, blank, blank), httpOk, notUpdateMsg),
                Arguments.of(updateReqBody(null, empty, empty, empty, empty), httpOk, notUpdateMsg),
                Arguments.of(updateReqBody(null, blank, blank, blank, blank), httpOk, notUpdateMsg),
                Arguments.of(updateReqBody(empty, null, empty, empty, empty), httpOk, notUpdateMsg),
                Arguments.of(updateReqBody(blank, null, blank, blank, blank), httpOk, notUpdateMsg),
                Arguments.of(reqBodyPassWithoutLowercaseLetter, httpOk, notUpdateMsg),
                Arguments.of(reqBodyPassWithoutUppercaseLetter, httpOk, notUpdateMsg),
                Arguments.of(reqBodyPassWithoutDigit, httpOk, notUpdateMsg),
                Arguments.of(reqBodyPassWithoutSpecialChar, httpOk, notUpdateMsg),
                Arguments.of(reqBodyPassShorterThan8Char, httpOk, notUpdateMsg),
                Arguments.of(updateReqBody(empty, empty, null, empty, empty), httpOk, notUpdateMsg),
                Arguments.of(updateReqBody(blank, blank, null, blank, blank), httpOk, notUpdateMsg),
                Arguments.of(updateReqBody(empty, empty, empty, null, empty), httpOk, notUpdateMsg),
                Arguments.of(updateReqBody(blank, blank, blank, null, blank), httpOk, notUpdateMsg),
                Arguments.of(updateReqBody(empty, empty, empty, empty, null), httpBad, invalidBody),
                Arguments.of(updateReqBody(blank, blank, blank, blank, null), httpBad, invalidBody),
                Arguments.of(updateReqBody(EMAIL, empty, empty, empty, empty), httpOk, notUpdateMsg),
                Arguments.of(updateReqBody(empty, PASSWORD, empty, empty, empty), httpOk, notUpdateMsg),
                Arguments.of(updateReqBody(empty, empty, FIRST_NAME, empty, empty), httpOk, notUpdateMsg),
                Arguments.of(updateReqBody(empty, empty, empty, SUR_NAMES, empty), httpOk, notUpdateMsg),
                Arguments.of(updateReqBody(empty, empty, empty, empty, BIRTHDATE_STR), httpOk, notUpdateMsg),
                Arguments.of(requestBodyFullWhitID(ID + "a", empty, empty, empty, empty, empty), httpUnAuth, unAuthMessage)
        );
    }

    private static String updateReqBody(String email, String password, String firstName, String surNames, String birthdate) {
        return requestBodyFullWhitID(ID, email, password, firstName, surNames, birthdate);
    }

    private static String updateReqBodyNewPass(String newPassword) {
        return requestBodyFullWhitIdAndNewPassword(newPassword);
    }

}
