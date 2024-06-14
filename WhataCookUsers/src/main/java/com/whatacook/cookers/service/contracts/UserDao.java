package com.whatacook.cookers.service.contracts;

import com.whatacook.cookers.model.users.UserDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Repository interface for accessing user data from MongoDB.
 * Extends ReactiveMongoRepository for reactive CRUD operations.
 * <p>
 * Methods:
 * - findByEmail(String email): Finds a user by their email.
 * - findBy_id(String _id): Finds a user by their ID.
 * - existsByEmail(String email): Checks if a user exists by their email.
 * <p>
 * Annotations:
 * - @Repository: Indicates that this interface is a Spring repository.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@Repository
public interface UserDao extends ReactiveMongoRepository<UserDto, String> {

    Mono<UserDto> findByEmail(String email);

    Mono<UserDto> findBy_id(String _id);

    Mono<Boolean> existsByEmail(String email);

}
