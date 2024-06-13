package com.whatacook.cookers.controller;

import com.whatacook.cookers.model.auth.ResetDto;
import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.constants.Htmls;
import com.whatacook.cookers.model.users.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResetPassCodeTest extends BaseTestClass {

    @Value("${app.endpoint.reset-pass}")
    private String usersResetPasswordEndpoint;
    private String htmlToTest;
    private ResetDto resetDto;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        pathVariable = usersEndpoint + usersResetPasswordEndpoint;
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
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(pathVariable)
                        .queryParam("resetCode", "invalidCode").build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(html -> assertEquals(htmlToTest, html));
    }

    @Test
    void testResetPassCodeInvalidAccountStatus() {
        userDTO.setAccountStatus(AccountStatus.OFF);
        String errorMsg = "Account Status Incorrect for this request: " + userDTO.getAccountStatus().getDetails();
        htmlToTest = Htmls.FailReset.get()
                .replace("errorDescriptionValue", errorMsg)
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("EMAIL_WAC", globalValues.getMailToWac());
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(pathVariable)
                        .queryParam("resetCode", resetDto.getCode()).build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(html -> assertEquals(htmlToTest, html));
    }

    @Test
    void testResetPassCodeExpired() {
        resetDto.setExpiration(resetDto.getExpiration().minusDays(1));
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(pathVariable)
                        .queryParam("resetCode", resetDto.getCode()).build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message").isEqualTo("This Code is Expired");
    }

    @Test
    void testResetPassCodeOK() {
        Mockito.when(resetDao.save(Mockito.any(ResetDto.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        Mockito.when(userDao.save(Mockito.any(UserDTO.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        ArgumentCaptor<ResetDto> resetDtoCaptor = ArgumentCaptor.forClass(ResetDto.class);

        AtomicReference<String> htmlResponse = new AtomicReference<>();

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(pathVariable)
                        .queryParam("resetCode", resetDto.getCode()).build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(htmlResponse::set);

        Mockito.verify(resetDao, Mockito.times(1)).save(resetDtoCaptor.capture());
        ResetDto resetDtoCaptured = resetDtoCaptor.getValue();

        String endPoint = globalValues.getUrlSetNewPassword() + resetDtoCaptured.getCode();
        htmlToTest = Htmls.FormToSendNewPassword.get()
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("RESET_CODE", resetDtoCaptured.getCode())
                .replace("ENDPOINT_RESET_PASS", endPoint);

        assertEquals(htmlToTest, htmlResponse.get());
    }
}
