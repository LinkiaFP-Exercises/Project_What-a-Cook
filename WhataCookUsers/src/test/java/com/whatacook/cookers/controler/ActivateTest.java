package com.whatacook.cookers.controler;

import com.whatacook.cookers.model.auth.ActivationDto;
import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.constants.Htmls;
import com.whatacook.cookers.model.users.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ActivateTest extends BaseTestClass {

    @Value("${app.endpoint.users-activate}")
    private String usersActivateEndpoint;
    private ArgumentCaptor<UserDTO> userCaptor;
    private ActivationDto activationDto;
    private String failHtmlToTest;

    @BeforeEach
    void setUp() {
        pathVariable = usersEndpoint + usersActivateEndpoint;
        userCaptor = ArgumentCaptor.forClass(UserDTO.class);
        activationDto = ActivationDto.to(userDtoBasicPending());
        Mockito.when(activationDao.findByCode(Mockito.eq(activationDto.getCode())))
                                                                .thenReturn(Mono.just(activationDto));
        Mockito.when(userDao.findBy_id(ID)).thenReturn(Mono.just(userDtoBasicPending()));
        Mockito.when(userDao.save(Mockito.any(UserDTO.class)))
                                    .thenAnswer(invocation -> Mono.just(invocation.getArgument(0)));
        Mockito.when(activationDao.deleteById(ID)).thenReturn(Mono.empty());
        failHtmlToTest = Htmls.FailActivation.get()
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("PATH_TO_RESEND", globalValues.getPathToResendActvationMail())
                .replace("EMAIL_WAC", globalValues.getMailToWac());
    }

    @Test
    void testActivationIsSuccess() {
        String htmlToTest = Htmls.SuccessActivation.get()
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("USER_NAME", FIRST_NAME);

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(pathVariable)
                    .queryParam("activationCode", activationDto.getCode()).build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> assertEquals(htmlToTest, html));

        // 1. Verificar que se llamó al método deleteById con el ID correcto
        Mockito.verify(activationDao, Mockito.times(1)).deleteById(activationDto.getId());
        // 2. Verificar que se llamó al método save del userDao
        Mockito.verify(userDao, Mockito.times(1)).save(userCaptor.capture());
        // 3. Verificar que el UserDTO guardado tenía el AccountStatus.OK
        UserDTO savedUser = userCaptor.getValue();
        assertEquals(AccountStatus.OK, savedUser.getAccountStatus());
    }

    @Test
    void testActivationCodeExpired() {
        activationDto.setExpiration(LocalDateTime.now().minusDays(2));
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(pathVariable)
                        .queryParam("activationCode", activationDto.getCode()).build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> assertEquals(failHtmlToTest, html));
        // Verificación adicional para asegurarse de que no se intenta borrar ni actualizar nada
        Mockito.verify(activationDao, Mockito.never()).deleteById(Mockito.anyString());
        Mockito.verify(userDao, Mockito.never()).save(Mockito.any(UserDTO.class));
    }

    @Test
    void testInvalidActivationCode() {
        // Configuración para que findByCode devuelva un Mono vacío, simulando un código no encontrado
        Mockito.when(activationDao.findByCode("invalidCode")).thenReturn(Mono.empty());
        // Ejecución del test endpoint con un código de activación no válido
        webTestClient.get().uri(uriBuilder -> uriBuilder.path(pathVariable)
                        .queryParam("activationCode", "invalidCode").build())
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .value(html -> assertEquals(failHtmlToTest, html));

        // Verificación adicional para asegurarse de que no se intenta borrar ni actualizar nada
        Mockito.verify(activationDao, Mockito.never()).deleteById(Mockito.anyString());
        Mockito.verify(userDao, Mockito.never()).save(Mockito.any(UserDTO.class));
    }

}
