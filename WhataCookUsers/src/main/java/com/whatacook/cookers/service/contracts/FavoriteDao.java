package com.whatacook.cookers.service.contracts;

import com.whatacook.cookers.model.favorites.FavoriteDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * Repository interface for accessing favorite data from MongoDB.
 * Extends ReactiveMongoRepository for reactive CRUD operations.
 * <p>
 * Annotations:
 * - None
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see FavoriteDto
 * @see ReactiveMongoRepository
 */
public interface FavoriteDao extends ReactiveMongoRepository<FavoriteDto, String> {
}
