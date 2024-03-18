package com.whatacook.cookers.controler;

import com.whatacook.cookers.config.SpringMailConfig;
import com.whatacook.cookers.config.jwt.JwtUtil;
import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.constants.Role;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.service.contracts.ActivationDao;
import com.whatacook.cookers.service.contracts.ResetDao;
import com.whatacook.cookers.service.contracts.UserDao;
import com.whatacook.cookers.utilities.GlobalValues;
import org.assertj.core.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BaseTestClass {

    @Autowired
    protected WebTestClient webTestClient;
    @Autowired
    protected SpringMailConfig springMailConfig;
    @Autowired
    protected GlobalValues globalValues;
    @Autowired
    protected JwtUtil jwtUtil;

    @MockBean
    protected UserDao userDao;
    @MockBean
    protected ActivationDao activationDao;
    @MockBean
    protected ResetDao resetDaoDAO;
    @MockBean
    protected JavaMailSender emailSender;



    protected static String requestBodyOnlyMail(String email) {
        return "{ \"email\": \"" + email + "\" }";
    }

    protected static String requestBodyFull(String email, String password, String firstName, String surNames, String birthdate) {
        return "{\n" +
                "    \"email\": \"" + email + "\",\n" +
                "    \"password\": \"" + password + "\",\n" +
                "    \"firstName\": \"" + firstName + "\",\n" +
                "    \"surNames\": \"" + surNames + "\",\n" +
                "    \"birthdate\": \"" + birthdate + "\"\n" +
                "}";
    }

    protected static String requestBodyFullyFill() {
        return requestBodyFull(EMAIL, PASSWORD, FIRST_NAME, SUR_NAMES, BIRTHDATE_STR);
    }

    protected static UserDTO userDtoBasicPending() {
        UserDTO userDTO = new UserDTO();
        userDTO.set_id(ID);
        userDTO.setRegistration(LOCAL_DATE_TIME);
        userDTO.setEmail(EMAIL);
        userDTO.setPassword(PASSWORD_ENCRYPT);
        userDTO.setFirstName(FIRST_NAME);
        userDTO.setSurNames(SUR_NAMES);
        userDTO.setBirthdate(BIRTHDATE);
        userDTO.setRoleType(Role.BASIC);
        userDTO.setAccountStatus(AccountStatus.PENDING);
        return userDTO;
    }
    protected static UserDTO userDtoBasicOk() {
        UserDTO userDTO = userDtoBasicPending();
        userDTO.setAccountStatus(AccountStatus.OK);
        return userDTO;
    }

    public static final String ID = "65db4a16e6cd946d5eb775fa";
    public static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.of(2024, 1, 20, 16, 20);
    public static final String EMAIL = "esgotilha@protonmail.ch";
    public static final String PASSWORD = "Test!234";
    public static final String PASSWORD_ENCRYPT = "$2a$10$FlrzGLiGkTe7blCuE6ZyMOwJ8Ru/D6aAmlvuQgJvRqd/cpCJvQUWa";
    public static final String FIRST_NAME = "Fulano";
    public static final String SUR_NAMES = "Ciclano Beltrano";
    public static final LocalDate BIRTHDATE = LocalDate.of(1982, 7, 19);
    public static final String BIRTHDATE_STR = "1982-07-19";

    void testFailRequest_400(String uri, String requestBody, boolean success, String message, String key) {
        webTestClient.post().uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestBody)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.success").isEqualTo(success)
                .jsonPath("$.message").value(text -> Assertions.assertThat(text).asString().contains(message))
                .jsonPath("$.content").value(content -> assertThat(content).asInstanceOf(MAP).containsKeys(key));
    }

}
