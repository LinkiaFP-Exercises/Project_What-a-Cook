package com.whatacook.cookers.service.contracts;

import com.whatacook.cookers.model.auth.ResetDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Repository interface for accessing reset data from MongoDB.
 * Extends ReactiveMongoRepository for reactive CRUD operations.
 * <p>
 * Methods:
 * - findByCode(String code): Finds a reset DTO by its code.
 * <p>
 * Annotations:
 * - @Repository: Indicates that this interface is a Spring repository.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see ResetDto
 * @see Mono
 * @see ReactiveMongoRepository
 */
@Repository
public interface ResetDao extends ReactiveMongoRepository<ResetDto, String> {

    Mono<ResetDto> findByCode(String code);

}
