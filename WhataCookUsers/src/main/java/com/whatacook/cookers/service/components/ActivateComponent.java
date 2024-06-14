package com.whatacook.cookers.service.components;

import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.constants.Htmls;
import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDto;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.utilities.GlobalValues;
import com.whatacook.cookers.service.ActivationService;
import com.whatacook.cookers.service.EmailService;
import com.whatacook.cookers.service.contracts.UserDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Component for handling user account activation processes.
 * <p>
 * Annotations:
 * - @AllArgsConstructor: Generates a constructor with 1 parameter for each field.
 * - @Component: Indicates that this class is a Spring component.
 * <p>
 * Fields:
 * - activationService: Service for activation operations.
 * - emailService: Service for email operations.
 * - globalValues: Global configuration values.
 * - DAO: Data Access Object for UserDto.
 * <p>
 * Methods:
 * - byActivationCodeSentByEmail(String activationCode): Activates a user account using the provided activation code.
 * - buildHtmlOkAccountActivatedContent(UserDto userDTO): Builds HTML content for successful account activation.
 * - buildHtmlFailAccountActivatedContent(Throwable error): Builds HTML content for failed account activation.
 * - resendActivationCode(String email): Resends the activation code to the user's email.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see ActivationService
 * @see EmailService
 * @see GlobalValues
 * @see UserDao
 * @see UserDto
 * @see UserJson
 * @see Mono
 * @see AllArgsConstructor
 * @see Component
 */
@AllArgsConstructor
@Component
public class ActivateComponent {

    private final ActivationService activationService;
    private final EmailService emailService;
    private final GlobalValues globalValues;
    private final UserDao DAO;

    /**
     * Activates a user account using the provided activation code.
     *
     * @param activationCode the activation code
     * @return HTML content indicating the result of the activation process
     */
    public Mono<String> byActivationCodeSentByEmail(String activationCode) {
        return Mono.just(activationCode)
                .flatMap(activationService::findByCode)
                .switchIfEmpty(UserServiceException.mono("This Code is Invalid"))
                .flatMap(activationDto -> {
                    if (ChronoUnit.HOURS.between(activationDto.getExpiration(), LocalDateTime.now()) > 24)
                        return UserServiceException.mono("This Code is Expired");
                    else
                        return Mono.just(activationDto);
                })
                .flatMap(activationDto ->
                        DAO.findBy_id(activationDto.getId())
                                .flatMap(userDTO -> {
                                    if (userDTO.getAccountStatus() == AccountStatus.PENDING) {
                                        userDTO.setAccountStatus(AccountStatus.OK);
                                        return DAO.save(userDTO)
                                                .then(activationService.deleteById(activationDto.getId()))
                                                .thenReturn(userDTO);
                                    } else {
                                        return UserServiceException.mono("The Account Status is not correct to activate account");
                                    }
                                }))
                .map(this::buildHtmlOkAccountActivatedContent)
                .onErrorResume(this::buildHtmlFailAccountActivatedContent);
    }

    /**
     * Builds HTML content for successful account activation.
     *
     * @param userDTO the user details
     * @return the HTML content
     */
    private String buildHtmlOkAccountActivatedContent(UserDto userDTO) {
        return Htmls.SuccessActivation.get()
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("USER_NAME", userDTO.getFirstName());
    }

    /**
     * Builds HTML content for failed account activation.
     *
     * @param error the error occurred
     * @return the HTML content
     */
    private Mono<String> buildHtmlFailAccountActivatedContent(Throwable error) {
        return Mono.just(Htmls.FailActivation.get()
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("PATH_TO_RESEND", globalValues.getPathToResendActvationMail())
                .replace("EMAIL_WAC", globalValues.getMailToWac()));
    }

    /**
     * Resends the activation code to the user's email.
     *
     * @param email the user's email
     * @return the user details
     */
    public Mono<UserJson> resendActivationCode(String email) {
        return DAO.findByEmail(email)
                .switchIfEmpty(UserServiceException.mono("This Email is Invalid"))
                .flatMap(userDTO -> activationService.findById(userDTO.get_id())
                        .flatMap(activationDto -> {
                            if (ChronoUnit.HOURS.between(activationDto.getExpiration(), LocalDateTime.now()) <= 24) {
                                return emailService.sendActivationMail(activationDto, userDTO);
                            } else {
                                return emailService.createActivationCodeAndSendEmail(userDTO);
                            }
                        }))
                .onErrorMap(UserServiceException::onErrorMap);
    }

}
