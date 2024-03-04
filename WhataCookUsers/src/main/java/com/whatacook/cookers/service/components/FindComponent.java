package com.whatacook.cookers.service.components;

import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.utilities.Util;
import com.whatacook.cookers.service.contracts.UserDao;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
@Validated
public class FindComponent {

    private final UserDao DAO;

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
                        .switchIfEmpty(UserServiceException.mono("This player does not exist or email is invalid!")))
                .map(UserDTO::toJson);
    }

}
