package com.whatacook.cookers.service.contracts;

import com.whatacook.cookers.model.auth.ActivationDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Repository interface for accessing activation data from MongoDB.
 * Extends ReactiveMongoRepository for reactive CRUD operations.
 * <p>
 * Methods:
 * - findByCode(String code): Finds an activation DTO by its code.
 * <p>
 * Annotations:
 * - @Repository: Indicates that this interface is a Spring repository.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see ActivationDto
 * @see Mono
 * @see ReactiveMongoRepository
 */
@Repository
public interface ActivationDao extends ReactiveMongoRepository<ActivationDto, String> {

    Mono<ActivationDto> findByCode(String code);

}
