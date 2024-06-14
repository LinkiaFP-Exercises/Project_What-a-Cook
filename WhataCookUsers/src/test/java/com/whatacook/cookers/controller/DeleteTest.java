package com.whatacook.cookers.controller;

import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.constants.Role;
import com.whatacook.cookers.model.users.UserDto;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;

import static com.whatacook.cookers.model.constants.AccountStatus.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DeleteTest extends BaseTestClass {


    @Value("${app.endpoint.users}")
    private String deleteOneEndpoint;

    private ArgumentCaptor<UserDto> argumentCaptor;
    private static UserDto userDTO = userDtoBasicOk();

    @BeforeEach
    void setUp() {
        argumentCaptor = ArgumentCaptor.forClass(UserDto.class);
        Mockito.when(userDao.findByEmail(EMAIL)).thenReturn(Mono.just(userDTO));
        Mockito.when(userDao.findBy_id(ID)).thenReturn(Mono.just(userDTO));
        Mockito.when(userDao.save(Mockito.any(UserDto.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArguments()[0]));
        Mockito.when(userDao.delete(Mockito.any(UserDto.class))).thenReturn(Mono.empty());
    }

    @Test
    @Order(1)
    void testDeleteOkStatus() {
        final String message = "REQUEST_DELETE set, you have one year to revoke the deletion";
        baseTestDeleteOkAndRequestDeleteStatus(message, REQUEST_DELETE);
    }
    @Test
    @Order(2)
    void testDeleteRequestStatus() {
        final String message = "MARKED_DELETE set, your account has been invalidated you have one year to request your data";
        userDTO.setRequestDeleteDate(userDTO.getRequestDeleteDate().minusYears(1));
        baseTestDeleteOkAndRequestDeleteStatus(message, MARKED_DELETE);
    }

    private void baseTestDeleteOkAndRequestDeleteStatus(String message, AccountStatus status) {
        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(deleteOneEndpoint)
                        .queryParam("id", ID).build())
                .header("Authorization", tokenUserOk())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo(message)
                .jsonPath("$.content.registration").isNotEmpty()
                .jsonPath("$.content.requestDeleteDate").isNotEmpty()
                .jsonPath("$.content._id").isEqualTo(ID)
                .jsonPath("$.content.email").isEqualTo(EMAIL)
                .jsonPath("$.content.firstName").isEqualTo(FIRST_NAME)
                .jsonPath("$.content.surNames").isEqualTo(SUR_NAMES)
                .jsonPath("$.content.birthdate").isEqualTo(BIRTHDATE_STR)
                .jsonPath("$.content.roleType").isEqualTo(Role.BASIC.get())
                .jsonPath("$.content.accountStatus").isEqualTo(status.toString())
                .jsonPath("$.content.accountStatusMsg").isEqualTo(status.getDetails());

        Mockito.verify(userDao).save(argumentCaptor.capture());
        userDTO = argumentCaptor.getValue();
    }

    @Test
    @Order(3)
    void testDeleteMarkedStatus() {
        final String message = "Your account is set to be deleted, but you still have time to request your data";
        userDTO.setRequestDeleteDate(userDTO.getRequestDeleteDate().minusYears(1).plusDays(1));
        baseTestDeleteMarkedDeleteAndDeleteStatus(message);
    }
    @Test
    @Order(4)
    void testDeleteDeleteStatus() {
        final String message = "Your account has been terminated";
        userDTO.setRequestDeleteDate(userDTO.getRequestDeleteDate().minusDays(1));
        baseTestDeleteMarkedDeleteAndDeleteStatus(message);
        Mockito.verify(userDao).delete(argumentCaptor.capture());
        userDTO = argumentCaptor.getValue();
    }

    private void baseTestDeleteMarkedDeleteAndDeleteStatus(String message) {
        webTestClient.delete().uri(uriBuilder -> uriBuilder.path(deleteOneEndpoint)
                        .queryParam("id", ID).build())
                .header("Authorization", tokenUserOk())
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message").isEqualTo(message);
    }

}
