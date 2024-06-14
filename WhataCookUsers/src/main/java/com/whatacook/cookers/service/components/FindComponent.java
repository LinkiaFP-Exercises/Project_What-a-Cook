package com.whatacook.cookers.service.components;

import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.users.UserDto;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.service.contracts.UserDao;
import com.whatacook.cookers.utilities.Util;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Component for handling user retrieval operations.
 * <p>
 * Annotations:
 * - @AllArgsConstructor: Generates a constructor with 1 parameter for each field.
 * - @Component: Indicates that this class is a Spring component.
 * <p>
 * Fields:
 * - DAO: Data Access Object for UserDto.
 * <p>
 * Methods:
 * - checkIfExistsByEmail(UserJson userJson): Checks if a user exists by email.
 * - findUserByEmail(UserJson userJson): Finds a user by email.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see UserDao
 * @see UserDto
 * @see UserJson
 * @see Mono
 * @see AllArgsConstructor
 * @see Component
 */
@AllArgsConstructor
@Component
public class FindComponent {

    private final UserDao DAO;

    /**
     * Checks if a user exists by email.
     *
     * @param userJson the user details
     * @return a Mono indicating if the user exists
     */
    public Mono<Boolean> checkIfExistsByEmail(UserJson userJson) {
        return Mono.just(userJson)
                .map(UserJson::getEmail)
                .filter(Util::isValidEmail)
                .flatMap(DAO::existsByEmail)
                .switchIfEmpty(UserServiceException.mono("Email not found!"))
                .doOnError(UserServiceException::onErrorMap);
    }

    /**
     * Finds a user by email.
     *
     * @param userJson the user details
     * @return a Mono of the user details
     */
    public Mono<UserJson> findUserByEmail(UserJson userJson) {
        return Mono.just(userJson)
                .map(UserJson::getEmail)
                .filter(Util::isValidEmail)
                .flatMap(DAO::findByEmail)
                .switchIfEmpty(UserServiceException.mono("This user does not exist or email is invalid!"))
                .map(UserDto::toJson)
                .doOnError(UserServiceException::onErrorMap);
    }

}
