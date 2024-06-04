package com.whatacook.cookers.controler;

import com.whatacook.cookers.model.constants.Htmls;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResetPassCodeTest extends BaseTestClass {

    @Value("${app.endpoint.reset-pass}")
    private String usersResetPasswordEndpoint;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testResetPassCodeNotFound() {
        Mockito.when(resetDao.findByCode(Mockito.anyString())).thenReturn(Mono.empty());
        String htmlToTest = Htmls.FailReset.get()
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("EMAIL_WAC", globalValues.getMailToWac())
                .replace("errorDescriptionValue", "Code Not Found");
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(usersResetPasswordEndpoint)
                        .queryParam("resetCode", "invalidCode").build())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(String.class)
                .value(html -> assertEquals(htmlToTest, html));
    }
}
