package com.whatacook.cookers.view;

import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.model.users.UserJson;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class ServiceComponentToActivate {

    private final ActivationService activationService;
    private final EmailService emailService;
    private final UserDAO DAO;

    public ServiceComponentToActivate(ActivationService activationService, EmailService emailService, UserDAO DAO) {
        this.activationService = activationService;
        this.emailService = emailService;
        this.DAO = DAO;
    }

    public Mono<UserJson> byActivationCodeSentByEmail(String activationCode) {
        return Mono.just(activationCode)
                .flatMap(activationService::findByCode)
                    .switchIfEmpty(Mono.error(UserServiceException.pull("This Code is Invalid")))
                .flatMap(activationDto -> {
                    if (ChronoUnit.HOURS.between(activationDto.getExpiration(), LocalDateTime.now()) > 24)
                        return Mono.error(UserServiceException.pull("This Code is Expired"));
                    else
                        return Mono.just(activationDto);
                })
                .flatMap(activationDto ->
                        DAO.findById(activationDto.getId())
                                .flatMap(userDTO -> {
                                    if (userDTO.getAccountStatus() == AccountStatus.PENDING) {
                                        userDTO.setAccountStatus(AccountStatus.OK);
                                        return DAO.save(userDTO)
                                                .then(activationService.deleteById(activationDto.getId()))
                                                .thenReturn(userDTO);
                                    } else {
                                        return Mono.error(UserServiceException.pull("The Account Status is not correct to activate account"));
                                    }
                                }))
                    .map(UserDTO::toJson)
                .onErrorMap(throwable -> UserServiceException.pull(throwable.getMessage()));
    }

    public Mono<UserJson> resendActivationCode(String email) {
        return DAO.findByEmail(email)
                    .switchIfEmpty(Mono.error(UserServiceException.pull("This Email is Invalid")))
                .flatMap(userDTO -> activationService.findById(userDTO.get_id())
                        .flatMap(activationDto -> {
                            if (ChronoUnit.HOURS.between(activationDto.getExpiration(), LocalDateTime.now()) <= 24) {
                                return emailService.sendActivationMail(activationDto, userDTO);
                            } else {
                                return emailService.createActivationCodeAndSendEmail(userDTO);
                            }
                        }))
                .onErrorMap(throwable -> UserServiceException.pull(throwable.getMessage()));
    }
}
