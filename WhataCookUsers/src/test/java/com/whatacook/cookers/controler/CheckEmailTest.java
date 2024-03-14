package com.whatacook.cookers.controler;

import com.whatacook.cookers.service.contracts.UserDao;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CheckEmailTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserDao DAO;

    @Value("${app.endpoint.users-check-email}")
    private String usersCheckEmailEndpoint;

    private static String requestBody(String email) {
        return "{ \"email\": \"" + email + "\" }";
    }

    @BeforeEach
    void setUp() {
        Mockito.when(DAO.existsByEmail(Mockito.anyString())).thenReturn(Mono.just(false));
    }

    @ParameterizedTest
    @CsvSource({
            "email@root.com, true, User already exists, true",
            "email@teste.com, false, User does not exist yet, false"
    })
    void testExistsByEmail_OK(String email, boolean exists, String message, boolean content) {
        Mockito.when(DAO.existsByEmail(email)).thenReturn(Mono.just(exists));
        webTestClient.post().uri(usersCheckEmailEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody(email))
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
        webTestClient.post().uri(usersCheckEmailEndpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(success)
                .jsonPath("$.message").value(text -> Assertions.assertThat(text).asString().contains(message))
                .jsonPath("$.content").value(content -> {
                    assertThat(content).isInstanceOf(Map.class);
                    assertThat(content).asInstanceOf(MAP).containsKeys(key);
                });
    }

    private static Stream<Arguments> provideVariablesForFailRequests() {
        String messageMail = "Invalid or incorrect format";
        String messageBody = "Invalid request body or not present";
        String badlyJson = "{\"email\":}";
        String mailKey = "email";
        String errorKey = "ERROR";
        return Stream.of(
                Arguments.of(requestBody("email@ro ot.com"), false, messageMail, mailKey),
                Arguments.of(requestBody(""), false, messageMail, mailKey),
                Arguments.of(requestBody(null), false, messageMail, mailKey),
                Arguments.of(badlyJson, false, messageBody, errorKey),
                Arguments.of("", false, messageBody, errorKey)
        );
    }

}
