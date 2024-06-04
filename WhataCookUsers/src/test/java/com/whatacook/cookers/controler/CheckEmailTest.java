package com.whatacook.cookers.controler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.stream.Stream;

public class CheckEmailTest extends BaseTestClass {

    @Value("${app.endpoint.users-check-email}")
    private String usersCheckEmailEndpoint;

    @BeforeEach
    void setUp() {
        Mockito.when(userDao.existsByEmail(Mockito.anyString())).thenReturn(Mono.just(false));
    }

    @ParameterizedTest
    @CsvSource({
            "email@root.com, true, User already exists, true",
            "email@teste.com, false, User does not exist yet, false"
    })
    void testExistsByEmail_OK(String email, boolean exists, String message, boolean content) {
        Mockito.when(userDao.existsByEmail(email)).thenReturn(Mono.just(exists));
        webTestClient.post().uri(usersCheckEmailEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBodyOnlyMail(email))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo(message)
                .jsonPath("$.content").isEqualTo(content);
    }

    @ParameterizedTest
    @MethodSource("provideVariablesForFailRequests")
    void testExistsByEmail_Fail(String requestBody, boolean success, String message, String key) {
        testFailRequest_400(usersCheckEmailEndpoint, requestBody, success, message, key);
    }

    private static Stream<Arguments> provideVariablesForFailRequests() {
        String messageMail = "Invalid or incorrect format";
        String messageBody = "Invalid request body or not present";
        String badlyJson = "{\"email\":}";
        String mailKey = "email";
        String errorKey = "ERROR";
        return Stream.of(
                Arguments.of(requestBodyOnlyMail("email@ro ot.com"), false, messageMail, mailKey),
                Arguments.of(requestBodyOnlyMail(empty), false, messageMail, mailKey),
                Arguments.of(requestBodyOnlyMail(null), false, messageMail, mailKey),
                Arguments.of(badlyJson, false, messageBody, errorKey),
                Arguments.of(empty, false, messageBody, errorKey)
        );
    }

}
