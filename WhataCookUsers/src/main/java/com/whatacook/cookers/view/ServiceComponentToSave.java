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

    public ServiceComponentToSave(UserDAO DAO)  {
        this.DAO = DAO;
    }

    public Mono<UserJson> saveUser(@Valid UserJustToSave userJson) {
        return Mono.just(userJson)
                .flatMap(this::validateAttributesInUserJson)
                .flatMap(this::checkEmailNotRegistered)
                .then(saveUserByJsonReturnJson(userJson));
    }

    private Mono<UserJustToSave> validateAttributesInUserJson(UserJustToSave userJson) {
        Map<String, Object> errors = new LinkedHashMap<>();

        if (isNullOrEmpty(userJson.getEmail()))
            errors.put("email", "E-mail is missing!");
        if (notValidEmail(userJson.getEmail()))
            errors.put("email", "This email has invalid format!");
        if (isNullOrEmpty(userJson.getPassword()))
            errors.put("password", "Password is missing!");
        if (isNullOrEmpty(userJson.getFirstName()))
            errors.put("firstName", "First Name is missing!");
        if (isNullOrEmpty(userJson.getSurNames()))
            errors.put("surNames", "Last Name is missing!");
        if (notValidBirthdate(userJson.getBirthdate()))
            errors.put("birthdate", "Missing or invalid format : 'YYYY-MM-DD' and more than 7 years!");

        if (!errors.isEmpty()) {
            return Mono.error(
                    UserServiceException.pull("Look in content for errors", errors));
        }

        return Mono.just(userJson);
    }

    private Mono<Void> checkEmailNotRegistered(UserJustToSave userJson) {
        return DAO.existsByEmail(userJson.getEmail())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        Map<String, Object> errors = new LinkedHashMap<>();
                        errors.put("email", "This email is already registered!");
                        return Mono.error(UserServiceException.pull("Look in content for errors", errors));
                    }
                    return Mono.empty();
                });
    }

    private Mono<UserJson> saveUserByJsonReturnJson(UserJustToSave userJson) {
        userJson.setFirstName(TitleCase(userJson.getFirstName()));
        userJson.setSurNames(TitleCase(userJson.getSurNames()));
        UserDTO userToSave = userJson.toUserDTO();
        return DAO.save(userToSave)
                .map(UserDTO::toJson);
    }

}
