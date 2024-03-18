package com.whatacook.cookers.controler;

import com.whatacook.cookers.model.users.UserDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginTest extends BaseTestClass {

    @Value("${security.jwt.login-url}")
    private String loginEndpoint;

    private static final String requestBody = "{" +
            "\"username\": \"" + EMAIL + "\"," +
            "\"password\": \"" + PASSWORD + "\"" +
            "}";


    @Test
    void testLoginIsSuccessful() {
        Mockito.when(userDao.findByEmail(Mockito.anyString())).thenReturn(Mono.just(userDtoBasicOk()));
        webTestClient.post().uri(loginEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo("TOKEN")
                .jsonPath("$.content").value(token -> {
                    assertNotNull(token);
                    assertTrue(jwtUtil.isValidToken(jwtUtil.getPrefix() + token));
                });
    }

    @Test
    void testLoginFailedPendingAccount() {
        String message = "Falta confirmar el e-mail para activar la cuenta";
        executeLoginTest(message, Mono.just(userDtoBasicPending()));
    }

    @Test
    void testLoginFailedUserNotFound() {
        String message = "USER NOT FOUND";
        executeLoginTest(message, Mono.empty());
    }

    private void executeLoginTest(String expectedMessagePart, Mono<UserDTO> userResponse) {
        Mockito.when(userDao.findByEmail(Mockito.anyString())).thenReturn(userResponse);

        webTestClient.post().uri(loginEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message").value(message -> Assertions.assertThat(message).asString().contains(expectedMessagePart));
    }

    @ParameterizedTest
    @MethodSource("provideVariablesForFailRequests")
    void testLoginIsBadRequest(String requestBody, boolean success, String message, String key) {
        testFailRequest_400(loginEndpoint, requestBody, success, message, key);
    }
    private static Stream<Arguments> provideVariablesForFailRequests() {
        String reqBodyWithoutPass = "{ \"username\": \"" + EMAIL + "\" }";
        String reqBodyWithoutUser = "{ \"password\": \"" + PASSWORD + "\" }";
        String keyPass = "password";
        String keyUser = "username";
        String validationError = "Validation error";
        String reqBodyBadJson = "{ \"username\": \"" + EMAIL + "\",\"password\": }";
        String reqBodyError = "Invalid request body or not present";
        String keyError = "ERROR";
        return Stream.of(
                Arguments.of(reqBodyWithoutPass, false, validationError, keyPass),
                Arguments.of(reqBodyWithoutUser, false, validationError, keyUser),
                Arguments.of("{}", false, validationError, keyPass),
                Arguments.of("{}", false, validationError, keyUser),
                Arguments.of(reqBodyBadJson, false, reqBodyError, keyError),
                Arguments.of("", false, reqBodyError, keyError)
        );
    }

}
