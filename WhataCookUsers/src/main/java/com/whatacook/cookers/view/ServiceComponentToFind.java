package com.whatacook.cookers.view;

import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.utilities.Util;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
@Validated
public class ServiceComponentToFind {

    private final UserDAO DAO;

    public Mono<Boolean> checkIfExistsByEmail(@Valid UserJson userJson) {
        return Mono.just(userJson)
                .map(UserJson::getEmail)
                .flatMap(email -> {
                    if (Util.isValidEmail(email))
                        return DAO.existsByEmail(email)
                                .switchIfEmpty(UserServiceException.mono("Email not found!"));
                    else
                        return UserServiceException.mono("Invalid email format!");
                });
    }


    public Mono<UserJson> findUserByEmail(UserJson userJson) {
        return Mono.just(userJson)
                .map(UserJson::getEmail)
                .filter(Util::isValidEmail)
                .flatMap(email -> DAO.findByEmail(email)
                        .switchIfEmpty(Mono.error(new UserServiceException("This player does not exist or email is invalid!"))))
                .map(UserDTO::toJson);
    }

}
