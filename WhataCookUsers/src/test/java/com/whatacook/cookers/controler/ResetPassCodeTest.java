package com.whatacook.cookers.controler;

import com.whatacook.cookers.model.auth.ResetDto;
import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.constants.Htmls;
import com.whatacook.cookers.model.users.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResetPassCodeTest extends BaseTestClass {

    @Value("${app.endpoint.reset-pass}")
    private String usersResetPasswordEndpoint;
    private ResetDto resetDto;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userDTO = userDtoBasicOk();
        resetDto = ResetDto.to(userDTO);
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
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(html -> assertEquals(htmlToTest, html));
    }

    @Test
    void testResetPassCodeInvalidAccountStatus() {
        userDTO.setAccountStatus(AccountStatus.OFF);
        Mockito.when(resetDao.findByCode(Mockito.anyString())).thenReturn(Mono.just(resetDto));
        Mockito.when(userDao.findBy_id(Mockito.anyString())).thenReturn(Mono.just(userDTO));
        String errorMsg = "Account Status Incorrect for this request: " + userDTO.getAccountStatus().getDetails();
        String htmlToTest = Htmls.FailReset.get()
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("EMAIL_WAC", globalValues.getMailToWac())
                .replace("errorDescriptionValue", errorMsg);
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(usersResetPasswordEndpoint)
                        .queryParam("resetCode", resetDto.getCode()).build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(html -> assertEquals(htmlToTest, html));
    }
}
