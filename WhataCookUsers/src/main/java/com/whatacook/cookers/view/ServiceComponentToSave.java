package com.whatacook.cookers.view;

import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.model.users.UserJson;
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

    Mono<UserJson> saveUser(@Valid UserJson userJson) throws UserServiceException {
        Map<String, String> validationErrors = validateUserJson(userJson);

        if (!validationErrors.isEmpty())
            throw UserServiceException.withErrors("Look in content for errors", validationErrors);

        checkEmailNotRegistered(userJson.getEmail());

        return saveUserByJsonReturnJson(userJson);
    }

    private Map<String, String> validateUserJson(UserJson userJson) {
        Map<String, String> errors = new LinkedHashMap<>();

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
            errors.put("birthdate", "Missing or invalid format : 'YYYY-MM-DD'!");

        return errors;
    }

    private Mono<Void> checkEmailNotRegistered(String email) {
        return DAO.existsByEmail(email)
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        Map<String, String> errors = new LinkedHashMap<>();
                        errors.put("email", "This email is already registered!");
                        return Mono.error(UserServiceException.withErrors("Look in content for errors", errors));
                    }
                    return Mono.empty();
                });
    }


    private Mono<UserJson> saveUserByJsonReturnJson(UserJson userJson) {
        userJson.setFirstName(TitleCase(userJson.getFirstName()));
        userJson.setSurNames(TitleCase(userJson.getSurNames()));
        UserDTO userToSave = userJson.toUserDTO();
        return DAO.save(userToSave)
                .map(UserDTO::toJson);
    }


}
