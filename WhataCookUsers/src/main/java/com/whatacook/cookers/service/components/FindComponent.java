package com.whatacook.cookers.service.components;

import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.service.contracts.UserDao;
import com.whatacook.cookers.utilities.Util;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
public class FindComponent {

    private final UserDao DAO;

    public Mono<Boolean> checkIfExistsByEmail(UserJson userJson) {
        return Mono.just(userJson)
                .map(UserJson::getEmail)
                .filter(Util::isValidEmail)
                .flatMap(DAO::existsByEmail)
                .switchIfEmpty(UserServiceException.mono("Email not found!"))
                .doOnError(UserServiceException::onErrorMap);
    }


    public Mono<UserJson> findUserByEmail(UserJson userJson) {
        return Mono.just(userJson)
                .map(UserJson::getEmail)
                .filter(Util::isValidEmail)
                .flatMap(DAO::findByEmail)
                .switchIfEmpty(UserServiceException.mono("This user does not exist or email is invalid!"))
                .map(UserDTO::toJson)
                .doOnError(UserServiceException::onErrorMap);
    }

}
