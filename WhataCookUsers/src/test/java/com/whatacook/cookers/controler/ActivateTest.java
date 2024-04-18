package com.whatacook.cookers.controler;

import com.whatacook.cookers.model.auth.ActivationDto;
import com.whatacook.cookers.model.constants.Htmls;
import com.whatacook.cookers.model.users.UserDTO;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActivateTest extends BaseTestClass {

    @Value("${app.endpoint.users-activate}")
    private String usersActivateEndpoint;
    private ArgumentCaptor<ActivationDto> activationCaptor;
    private ArgumentCaptor<UserDTO> userCaptor;
    private ArgumentCaptor<MimeMessage> mimeMessageCaptor;
    private ActivationDto activationDto;

    @BeforeEach
    void setUp() {
        activationCaptor = ArgumentCaptor.forClass(ActivationDto.class);
        userCaptor = ArgumentCaptor.forClass(UserDTO.class);
        mimeMessageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        activationDto = ActivationDto.to(userDtoBasicPending());
        Mockito.when(activationDao.findByCode(Mockito.eq(activationDto.getCode()))).thenReturn(Mono.just(activationDto));
        Mockito.when(userDao.findBy_id(ID)).thenReturn(Mono.just(userDtoBasicPending()));
        Mockito.when(userDao.save(Mockito.any(UserDTO.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        Mockito.when(activationDao.deleteById(ID)).thenReturn(Mono.empty());
    }

    @Test
    void testActivationIsSuccess() {
        String htmlToTest = Htmls.SuccessActivation.get()
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("USER_NAME", FIRST_NAME);

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(usersActivateEndpoint)
                    .queryParam("activationCode", activationDto.getCode()).build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> assertEquals(htmlToTest, html));

    }
}
