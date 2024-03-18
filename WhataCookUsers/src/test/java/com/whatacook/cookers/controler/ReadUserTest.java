package com.whatacook.cookers.controler;

import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.constants.Role;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

public class ReadUserTest extends BaseTestClass {

    @Value("${app.endpoint.find-by-email}")
    private String readOneEndpoint;

    @BeforeEach
    void setUp() {
        Mockito.when(userDao.findByEmail(EMAIL)).thenReturn(Mono.just(userDtoBasicOk()));
    }

    @Test
    void testReadUserByUser() {
        baseTestReadUserByEmail_Ok(tokenUserOk());
    }

    @Test
    void testReadUserByAdmin() {
        String emailAdmin = "admin@test.com";
        Mockito.when(userDao.findByEmail(emailAdmin)).thenReturn(Mono.just(userDtoAdminOk(emailAdmin)));
        baseTestReadUserByEmail_Ok(tokenAdminOk(emailAdmin));
    }

    void baseTestReadUserByEmail_Ok(String token) {
        webTestClient.post().uri(readOneEndpoint)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBodyOnlyMail(EMAIL))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo("User successfully read")
                .jsonPath("$.content._id").isEqualTo(ID)
                .jsonPath("$.content.email").isEqualTo(EMAIL)
                .jsonPath("$.content.firstName").isEqualTo(FIRST_NAME)
                .jsonPath("$.content.surNames").isEqualTo(SUR_NAMES)
                .jsonPath("$.content.birthdate").isEqualTo(BIRTHDATE_STR)
                .jsonPath("$.content.roleType").isEqualTo(Role.BASIC.get())
                .jsonPath("$.content.accountStatus").isEqualTo(AccountStatus.OK.toString())
                .jsonPath("$.content.accountStatusMsg").isEqualTo(AccountStatus.OK.getDetails());
    }


    @Test
    void testReadUserByOtherUser() {
        final String unAuthMessage = "No tienes permiso para acceder a esta información";
        String emailTest = "other@email.com";
        Mockito.when(userDao.findByEmail(emailTest)).thenReturn(Mono.just(userDtoOtherOk(emailTest)));
        baseTestReadUserByEmail_Fail(tokenOtherUserOk(emailTest), unAuthMessage);
    }

    @Test
    void testReadUserByUserExpiredToken() {
        final String unAuthMessage = "Token expired. Please login again";
        baseTestReadUserByEmail_Fail(tokenExpired(EMAIL), unAuthMessage);
    }

    @Test
    void testReadUserByUserInvalidToken() {
        final String unAuthMessage = "Invalid token";
        baseTestReadUserByEmail_Fail("a" + tokenUserOk(), unAuthMessage);
    }

    void baseTestReadUserByEmail_Fail(String token, String message) {
        webTestClient.post().uri(readOneEndpoint)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBodyOnlyMail(EMAIL))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message").value(text -> Assertions.assertThat(text).asString().contains(message));
    }


}
