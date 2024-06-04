package com.whatacook.cookers.controler;

import com.whatacook.cookers.model.auth.ResetDto;
import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.constants.Htmls;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.utilities.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SetNewPasswordTest extends BaseTestClass {

    @Value("${app.endpoint.set-new-pass}")
    private String usersSetNewPasswordEndpoint;
    private String htmlToTest;
    private ResetDto resetDto;
    private UserDTO userDTO;
    private String newPassword;
    private String body;

    @BeforeEach
    void setUp() {
        userDTO = userDtoBasicOk();
        resetDto = ResetDto.to(userDTO);
        newPassword = "teste!2PASWORD";
        body = "{\n" +
                "    \"_id\": \"" + resetDto.getCode() + "\",\n" +
                "    \"newPassword\": \""+ newPassword +"\"\n" +
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

    @Test
    void testSetNewPassWithInvalidCode() {
        userDTO.setPassword(Util.encryptPassword(resetDto.getCode()));
        htmlToTest = Htmls.FailSetNewPassword.get()
                .replace("errorDescriptionValue", "Reset code is invalid")
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("URL_FORGOT_PASS", globalValues.getUrlForgotPassword())
                .replace("EMAIL_WAC", globalValues.getMailToWac());
        webTestClient.post().uri(uriBuilder -> uriBuilder.path(usersSetNewPasswordEndpoint)
                        .queryParam("codeToSet", resetDto.getCode()).build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body.replace(resetDto.getCode(), "invalidCode"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> assertEquals(htmlToTest, html));
    }

    @Test
    void testSetNewPassWithInvalidPassword() {
        userDTO.setPassword(Util.encryptPassword(resetDto.getCode()));
        htmlToTest = Htmls.FailSetNewPassword.get()
                .replace("errorDescriptionValue", "Reset code is invalid")
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("URL_FORGOT_PASS", globalValues.getUrlForgotPassword())
                .replace("EMAIL_WAC", globalValues.getMailToWac());
        webTestClient.post().uri(uriBuilder -> uriBuilder.path(usersSetNewPasswordEndpoint)
                        .queryParam("codeToSet", resetDto.getCode()).build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body.replace("teste!2PASWORD", "invalidPass"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> assertEquals(htmlToTest, html));
    }

    @Test
    void testSetNewPassOk() {
        userDTO.setPassword(Util.encryptPassword(resetDto.getCode()));
        htmlToTest = Htmls.SuccessSetNewPassword.get()
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("USER_NAME", userDTO.getFirstName());

        Mockito.when(userDao.save(Mockito.any(UserDTO.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        ArgumentCaptor<UserDTO> userDtoCaptor = ArgumentCaptor.forClass(UserDTO.class);

        Mockito.when(resetDao.deleteById(Mockito.anyString())).thenReturn(Mono.empty());

        webTestClient.post().uri(uriBuilder -> uriBuilder.path(usersSetNewPasswordEndpoint)
                        .queryParam("codeToSet", resetDto.getCode()).build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> assertEquals(htmlToTest, html));

        Mockito.verify(userDao, Mockito.times(1)).save(userDtoCaptor.capture());
        UserDTO userDtoCaptured = userDtoCaptor.getValue();
        Mockito.verify(resetDao, Mockito.times(1)).deleteById(Mockito.anyString());
        assertTrue(Util.encryptMatches(newPassword, userDtoCaptured.getPassword()));
    }

}
