package com.whatacook.cookers.controler;

import com.whatacook.cookers.model.auth.ResetDto;
import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.constants.Htmls;
import com.whatacook.cookers.model.users.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SetNewPasswordTest extends BaseTestClass {

    @Value("${app.endpoint.set-new-pass}")
    private String usersSetNewPasswordEndpoint;
    private String htmlToTest;
    private ResetDto resetDto;
    private UserDTO userDTO;
    private String body;

    @BeforeEach
    void setUp() {
        userDTO = userDtoBasicOk();
        resetDto = ResetDto.to(userDTO);
        body = "{\n" +
                "    \"_id\": \"" + resetDto.getCode() + "\",\n" +
                "    \"newPassword\": \"teste!2PASWORD\"\n" +
                "}";
        Mockito.when(resetDao.findByCode(Mockito.anyString())).thenReturn(Mono.just(resetDto));
        Mockito.when(userDao.findBy_id(Mockito.anyString())).thenReturn(Mono.just(userDTO));
    }

    @Test
    void testResetPassCodeNotFound() {
        Mockito.when(resetDao.findByCode(Mockito.anyString())).thenReturn(Mono.empty());
        htmlToTest = Htmls.FailSetNewPassword.get()
                .replace("errorDescriptionValue", "Code Not Found")
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("URL_FORGOT_PASS", globalValues.getUrlForgotPassword())
                .replace("EMAIL_WAC", globalValues.getMailToWac());
        webTestClient.post().uri(uriBuilder -> uriBuilder.path(usersSetNewPasswordEndpoint)
                        .queryParam("codeToSet", "invalidCode").build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(html -> assertEquals(htmlToTest, html));
    }

    @Test
    void testSetNewPassWithInvalidAccountStatus() {
        userDTO.setAccountStatus(AccountStatus.OFF);
        String errorMsg = "Account Status Incorrect for this request: " + userDTO.getAccountStatus().getDetails();
        htmlToTest = Htmls.FailSetNewPassword.get()
                .replace("errorDescriptionValue", errorMsg)
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("URL_FORGOT_PASS", globalValues.getUrlForgotPassword())
                .replace("EMAIL_WAC", globalValues.getMailToWac());
        webTestClient.post().uri(uriBuilder -> uriBuilder.path(usersSetNewPasswordEndpoint)
                        .queryParam("codeToSet", resetDto.getCode()).build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(html -> assertEquals(htmlToTest, html));
    }

    @Test
    void testSetNewPassWithCodeExpired() {
        resetDto.setExpiration(resetDto.getExpiration().minusDays(1));
        htmlToTest = Htmls.FailSetNewPassword.get()
                .replace("errorDescriptionValue", "This Code is Expired")
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("URL_FORGOT_PASS", globalValues.getUrlForgotPassword())
                .replace("EMAIL_WAC", globalValues.getMailToWac());
        webTestClient.post().uri(uriBuilder -> uriBuilder.path(usersSetNewPasswordEndpoint)
                        .queryParam("codeToSet", resetDto.getCode()).build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> assertEquals(htmlToTest, html));
    }

}
