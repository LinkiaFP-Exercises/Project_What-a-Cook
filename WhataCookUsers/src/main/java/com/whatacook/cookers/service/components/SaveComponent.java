package com.whatacook.cookers.service.components;

import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDto;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.model.users.UserJustToSave;
import com.whatacook.cookers.service.EmailService;
import com.whatacook.cookers.service.contracts.UserDao;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.whatacook.cookers.utilities.Util.*;

@AllArgsConstructor
@Validated
@Component
public class SaveComponent {

    private final UserDao DAO;
    private final EmailService emailService;

    public Mono<UserJson> saveUser(@Valid UserJustToSave userJustToSave) {
        return Mono.just(userJustToSave)
                .flatMap(this::validateAttributes)
                .flatMap(this::checkEmailNotRegistered)
                .flatMap(this::saveUserByJtsReturnDto)
                .flatMap(emailService::createActivationCodeAndSendEmail);
    }

    private Mono<UserJustToSave> validateAttributes(UserJustToSave userJustToSave) {
        Map<String, Object> errors = new LinkedHashMap<>();

        if (isNullOrEmptyOrLiteralNull(userJustToSave.getEmail()))
            errors.put("email", "E-mail is missing!");
        if (notValidEmail(userJustToSave.getEmail()))
            errors.put("email", "This email has invalid format!");
        if (isNullOrEmptyOrLiteralNull(userJustToSave.getPassword()))
            errors.put("password", "Password is missing!");
        if (notValidPassword(userJustToSave.getPassword()))
            errors.put("password", "Password is invalid format: 8 characters " +
                    "-> uppercase lowercase letters numbers and special characters !");
        if (isNullOrEmptyOrLiteralNull(userJustToSave.getFirstName()))
            errors.put("firstName", "First Name is missing!");
        if (isNullOrEmptyOrLiteralNull(userJustToSave.getSurNames()))
            errors.put("surNames", "Last Name is missing!");
        if (notValidBirthdate(userJustToSave.getBirthdate()))
            errors.put("birthdate", "Missing or invalid format : 'YYYY-MM-DD' and more than 7 years!");

        if (!errors.isEmpty())
            return UserServiceException.mono("Look in content for errors", errors);
        else
            return Mono.just(userJustToSave);
    }

    private Mono<UserJustToSave> checkEmailNotRegistered(UserJustToSave userJustToSave) {
        return DAO.existsByEmail(userJustToSave.getEmail())
                .flatMap(exists -> exists
                        ? UserServiceException.mono("This email is already registered!")
                        : Mono.just(userJustToSave));
    }

    private Mono<UserDto> saveUserByJtsReturnDto(UserJustToSave userJustToSave) {
        return Mono.just(userJustToSave)
                .flatMap(user -> {
                    user.setFirstName(TitleCase(user.getFirstName()));
                    user.setSurNames(TitleCase(user.getSurNames()));
                    return Mono.just(user);
                }).map(UserJustToSave::toUserDTO)
                .flatMap(DAO::save)
                .doOnError(UserServiceException::doOnErrorMap);
    }

}
