package com.whatacook.cookers.service.contracts;

import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.model.users.UserJson;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Mono;

/**
 * Interface for accessing user-related data and operations.
 * Extends ReactiveUserDetailsService for user authentication details.
 * <p>
 * Methods:
 * - existsByEmail(UserJson userJson): Checks if a user exists by their email.
 * - readOne(UserJson userJson): Reads a user by their details.
 * - updateOne(UserJson userJson): Updates a user by their details.
 * - deleteOne(UserJson userJson): Deletes a user by their details.
 * <p>
 * Annotations:
 * - None
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see ReactiveUserDetailsService
 * @see Response
 * @see UserJson
 */
public interface UserAccessContractModel extends ReactiveUserDetailsService {

    Mono<Response> existsByEmail(UserJson userJson);

//    Mono<Response> createOne(UserJustToSave userJson);
//    transferred for authentication service

    Mono<Response> readOne(UserJson userJson);

    Mono<Response> updateOne(UserJson userJson);

    Mono<Response> deleteOne(UserJson userJson);

}
