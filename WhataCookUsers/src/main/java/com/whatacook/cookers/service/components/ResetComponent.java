package com.whatacook.cookers.service.components;

import com.whatacook.cookers.model.auth.ResetDto;
import com.whatacook.cookers.model.constants.AccountStatus;
import com.whatacook.cookers.model.constants.Htmls;
import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDto;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.service.ResetService;
import com.whatacook.cookers.service.contracts.UserDao;
import com.whatacook.cookers.utilities.GlobalValues;
import com.whatacook.cookers.utilities.Util;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static com.whatacook.cookers.utilities.Util.encryptPassword;

/**
 * Component for handling password reset operations.
 * <p>
 * Annotations:
 * - @AllArgsConstructor: Generates a constructor with 1 parameter for each field.
 * - @Component: Indicates that this class is a Spring component.
 * <p>
 * Fields:
 * - resetService: Service for handling reset operations.
 * - globalValues: Global configuration values.
 * - DAO: Data Access Object for UserDto.
 * <p>
 * Methods:
 * - resetPasswordByCodeAndReturnNewPassForm(String resetCode): Resets password by code and returns a new password form.
 * - setNewPasswordByCode(UserJson userJson): Sets a new password by code.
 * - buildHtmlFormToSendNewPassword(ResetDto resetDto): Builds HTML form for sending new password.
 * - buildHtmlSuccessSetNewPassword(UserDto userDTO): Builds HTML for successful password reset.
 * - buildHtmlFailSetNewPassword(Throwable throwable): Builds HTML for failed password reset.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see ResetService
 * @see GlobalValues
 * @see UserDao
 * @see UserDto
 * @see UserJson
 * @see Mono
 * @see AllArgsConstructor
 * @see Component
 * @see ChronoUnit
 * @see LocalDateTime
 * @see Htmls
 * @see Util
 * @see UserServiceException
 * @see AccountStatus
 */
@AllArgsConstructor
@Component
public class ResetComponent {

    private final ResetService resetService;
    private final GlobalValues globalValues;
    private final UserDao DAO;

    /**
     * Resets password by code and returns a new password form.
     *
     * @param resetCode the reset code
     * @return a Mono of the HTML form
     */
    public Mono<String> resetPasswordByCodeAndReturnNewPassForm(String resetCode) {
        return Mono.just(resetCode)
                .flatMap(resetService::findByCode)
                .switchIfEmpty(UserServiceException.mono("This Code is Invalid"))
                .flatMap(resetDto -> {
                    if (ChronoUnit.HOURS.between(resetDto.getExpiration(), LocalDateTime.now()) > 1)
                        return UserServiceException.mono("This Code is Expired");
                    else
                        return Mono.just(resetDto);
                }).flatMap(resetDto -> DAO.findBy_id(resetDto.getId())
                        .flatMap(userDTO -> resetService.createNew(userDTO)
                                .flatMap(newCode -> {
                                    userDTO.setPassword(encryptPassword(newCode.getCode()));
                                    userDTO.setAccountStatus(AccountStatus.OUTDATED);
                                    return DAO.save(userDTO).thenReturn(newCode);
                                })))
                .map(this::buildHtmlFormToSendNewPassword)
                .onErrorMap(UserServiceException::onErrorMap);
    }

    /**
     * Builds HTML form for sending new password.
     *
     * @param resetDto the reset details
     * @return the HTML form
     */
    private String buildHtmlFormToSendNewPassword(ResetDto resetDto) {
        String endPoint = globalValues.getUrlSetNewPassword() + resetDto.getCode();
        return Htmls.FormToSendNewPassword.get()
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("RESET_CODE", resetDto.getCode())
                .replace("ENDPOINT_RESET_PASS", endPoint);
    }

    /**
     * Sets a new password by code.
     *
     * @param userJson the user details
     * @return a Mono of the HTML response
     */
    public Mono<String> setNewPasswordByCode(UserJson userJson) {
        return resetService.findByCode(userJson.get_id())
                .switchIfEmpty(UserServiceException.mono("No user was found"))
                .flatMap(resetDto -> {
                    if (ChronoUnit.HOURS.between(resetDto.getExpiration(), LocalDateTime.now()) > 1)
                        return UserServiceException.mono("This Code is Expired");
                    else
                        return DAO.findBy_id(resetDto.getId());
                })
                .flatMap(userDTO -> {
                    // To not expose the ID, the ID provided is the same reset password code
                    if (Util.encryptMatches(userJson.get_id(), userDTO.getPassword())
                            && Util.isValidPassword(userJson.getNewPassword())) {
                        userDTO.setPassword(encryptPassword(userJson.getNewPassword()));
                        return DAO.save(userDTO);
                    } else
                        return UserServiceException.mono("Reset code is invalid");
                }).flatMap(this::buildHtmlSuccessSetNewPassword)
                .onErrorResume(this::buildHtmlFailSetNewPassword);
    }

    /**
     * Builds HTML for successful password reset.
     *
     * @param userDTO the user details
     * @return a Mono of the HTML response
     */
    private Mono<String> buildHtmlSuccessSetNewPassword(UserDto userDTO) {
        return resetService.deleteById(userDTO.get_id())
                .then(Mono.fromCallable(() -> Htmls.SuccessSetNewPassword.get()
                        .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                        .replace("USER_NAME", userDTO.getFirstName())));
    }

    /**
     * Builds HTML for failed password reset.
     *
     * @param throwable the error
     * @return a Mono of the HTML response
     */
    private Mono<String> buildHtmlFailSetNewPassword(Throwable throwable) {
        return Mono.just(Htmls.FailSetNewPassword.get()
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("EMAIL_WAC", globalValues.getMailToWac())
                .replace("URL_FORGOT_PASS", globalValues.getUrlForgotPassword())
                .replace("errorDescriptionValue", throwable.getMessage()));
    }

}
