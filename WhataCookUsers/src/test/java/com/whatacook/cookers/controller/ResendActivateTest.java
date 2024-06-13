package com.whatacook.cookers.controller;

import com.whatacook.cookers.model.auth.ActivationDto;
import com.whatacook.cookers.model.constants.Role;
import com.whatacook.cookers.model.users.UserDTO;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;


public class ResendActivateTest extends BaseTestClass {

    @Value("${app.endpoint.users-resend}")
    private String usersResendActivationEndpoint;
    private ActivationDto activationDto;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        pathVariable = usersEndpoint + usersResendActivationEndpoint;
        userDTO = userDtoBasicPending();
        activationDto = ActivationDto.to(userDTO);
        mimeMessageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        activationCaptor = ArgumentCaptor.forClass(ActivationDto.class);
        Mockito.when(userDao.findByEmail(userDTO.getEmail())).thenReturn(Mono.just(userDTO));
        Mockito.when(userDao.findBy_id(userDTO.get_id())).thenReturn(Mono.just(userDTO));
        Mockito.when(emailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    }

    @Test
    void testResendActivationEmailNotFound() {
        Mockito.when(userDao.findByEmail(Mockito.anyString())).thenReturn(Mono.empty());

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(pathVariable)
                        .queryParam("emailToResend", "invalidMail").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message").isEqualTo("Email not found.");
    }

    @Test
    void testResendActivationOkWithCodeNotExpired() {
        Mockito.when(activationDao.findById(userDTO.get_id())).thenReturn(Mono.just(activationDto));
        webTestClientForTestResendActivationOk();
        mokitoVerifyEmailSenderAndCompareActivationCodeAndmimeMsg(activationDto);
    }

    @Test
    void testResendActivationOkWithCodeExpired() {
        activationDto.setExpiration(activationDto.getExpiration().minusDays(2));
        Mockito.when(activationDao.findById(userDTO.get_id())).thenReturn(Mono.just(activationDto));
        Mockito.when(activationDao.save(Mockito.any(ActivationDto.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        webTestClientForTestResendActivationOk();
        ActivationDto capturedActivation = mokitoVerifyActvationDaoSaveAndAssertActivationCode(activationDto.getCode());
        mokitoVerifyEmailSenderAndCompareActivationCodeAndmimeMsg(capturedActivation);
    }

    private void webTestClientForTestResendActivationOk() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(pathVariable)
                        .queryParam("emailToResend", userDTO.getEmail()).build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo("Activation mail successfully resented")
                .jsonPath("$.content.email").isEqualTo(EMAIL)
                .jsonPath("$.content.surNames").isEqualTo(SUR_NAMES)
                .jsonPath("$.content.birthdate").isEqualTo(BIRTHDATE_STR)
                .jsonPath("$.content.roleType").isEqualTo(Role.BASIC.get())
                .jsonPath("$.content.accountStatus").isEqualTo(userDTO.getAccountStatus().toString())
                .jsonPath("$.content.accountStatusMsg").isEqualTo(userDTO.getAccountStatus().getDetails());
    }
}
