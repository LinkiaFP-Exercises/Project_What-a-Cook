package com.whatacook.cookers.controler;

import com.whatacook.cookers.model.users.UserDTO;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

import static com.whatacook.cookers.model.constants.AccountStatus.*;
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

    @ParameterizedTest
    @MethodSource("provideVariablesForLoginRequests")
    void executeLoginTest(String expectedMessagePart, Mono<UserDTO> userResponse) {
        Mockito.when(userDao.findByEmail(Mockito.anyString())).thenReturn(userResponse);
        Mockito.when(userDao.delete(Mockito.any(UserDTO.class))).thenReturn(Mono.empty());

        webTestClient.post().uri(loginEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message").value(message ->
                        Assertions.assertThat(message).asString().contains(expectedMessagePart));
    }

    private static Stream<Arguments> provideVariablesForLoginRequests() {
        return Stream.of(
                Arguments.of("USER NOT FOUND", Mono.empty()),
                Arguments.of(PENDING.getDetails(), Mono.just(userDtoBasicPending())),
                Arguments.of(OFF.getDetails(), Mono.just(userDtoBasicAccountStatus(OFF))),
                Arguments.of(OUTDATED.getDetails(), Mono.just(userDtoBasicAccountStatus(OUTDATED))),
                Arguments.of(REQUEST_DELETE.getDetails(), Mono.just(userDtoBasicAccountStatus(REQUEST_DELETE))),
                Arguments.of(MARKED_DELETE.getDetails(), Mono.just(userDtoBasicAccountStatus(MARKED_DELETE))),
                Arguments.of(DELETE.getDetails(), Mono.just(userDtoBasicAccountStatus(DELETE)))
        );
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
                Arguments.of(empty, false, reqBodyError, keyError)
        );
    }

}
