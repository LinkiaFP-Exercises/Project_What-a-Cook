package com.whatacook.cookers.controler;

import com.whatacook.cookers.model.auth.ResetDto;
import com.whatacook.cookers.model.constants.Htmls;
import com.whatacook.cookers.model.users.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SetNewPasswordTest extends BaseTestClass {

    @Value("${app.endpoint.set-new-pass}")
    private String usersSetNewPasswordEndpoint;
    private String htmlToTest;
    private ResetDto resetDto;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = userDtoBasicOk();
        resetDto = ResetDto.to(userDTO);
        Mockito.when(resetDao.findByCode(Mockito.anyString())).thenReturn(Mono.just(resetDto));
        Mockito.when(userDao.findBy_id(Mockito.anyString())).thenReturn(Mono.just(userDTO));
    }

    @Test
    void testResetPassCodeNotFound() {
        Mockito.when(resetDao.findByCode(Mockito.anyString())).thenReturn(Mono.empty());
        htmlToTest = Htmls.FailReset.get()
                .replace("errorDescriptionValue", "Code Not Found")
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("EMAIL_WAC", globalValues.getMailToWac());
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(usersSetNewPasswordEndpoint)
                        .queryParam("resetCode", "invalidCode").build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(html -> assertEquals(htmlToTest, html));
    }

}
