package com.whatacook.cookers.view;

import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.model.users.UserJustToSave;
import jakarta.validation.Valid;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.whatacook.cookers.utilities.Util.*;

@Validated
@Component
public class ServiceComponentToSave {

    private final UserDAO DAO;
    private final EmailService emailService;

    public ServiceComponentToSave(UserDAO DAO, EmailService emailService)  {
        this.DAO = DAO;
        this.emailService = emailService;
    }

    public Mono<UserJson> saveUser(@Valid UserJustToSave userJustToSave) {
        return Mono.just(userJustToSave)
                .flatMap(this::validateAttributesInUserJson)
                .flatMap(this::checkEmailNotRegistered)
                .flatMap(this::saveUserByJtsReturnDto)
                .flatMap(emailService::sendActivationEmail);
    }

    private Mono<UserJustToSave> validateAttributesInUserJson(UserJustToSave userJustToSave) {
        Map<String, Object> errors = new LinkedHashMap<>();

        if (isNullOrEmpty(userJustToSave.getEmail()))
            errors.put("email", "E-mail is missing!");
        if (notValidEmail(userJustToSave.getEmail()))
            errors.put("email", "This email has invalid format!");
        if (isNullOrEmpty(userJustToSave.getPassword()))
            errors.put("password", "Password is missing!");
        if (isNullOrEmpty(userJustToSave.getFirstName()))
            errors.put("firstName", "First Name is missing!");
        if (isNullOrEmpty(userJustToSave.getSurNames()))
            errors.put("surNames", "Last Name is missing!");
        if (notValidBirthdate(userJustToSave.getBirthdate()))
            errors.put("birthdate", "Missing or invalid format : 'YYYY-MM-DD' and more than 7 years!");

        if (!errors.isEmpty()) {
            return Mono.error(
                    UserServiceException.pull("Look in content for errors", errors));
        }

        return Mono.just(userJustToSave);
    }

    private Mono<UserJustToSave> checkEmailNotRegistered(UserJustToSave userJustToSave) {
        return DAO.existsByEmail(userJustToSave.getEmail())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        Map<String, Object> errors = new LinkedHashMap<>();
                        errors.put("email", "This email is already registered!");
                        return Mono.error(UserServiceException.pull("Look in content for errors", errors));
                    }
                    return Mono.just(userJustToSave);
                });
    }

    private Mono<UserDTO> saveUserByJtsReturnDto(UserJustToSave userJustToSave) {
        userJustToSave.setFirstName(TitleCase(userJustToSave.getFirstName()));
        userJustToSave.setSurNames(TitleCase(userJustToSave.getSurNames()));
        UserDTO userToSave = userJustToSave.toUserDTO();
        return DAO.save(userToSave)
                .doOnError(throwable -> UserServiceException.throwUp(throwable.getMessage()));
    }

}
