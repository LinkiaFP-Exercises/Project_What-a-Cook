package linkia.dam.whatacookrecipes.service.repository;

import linkia.dam.whatacookrecipes.model.IngredientDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository interface for managing {@link IngredientDto} entities.
 * Extends the {@link ReactiveMongoRepository} to provide reactive CRUD operations.
 * <p>
 * Methods:
 * - findByNameContainingIgnoreCase(String name): Finds ingredients with names containing the specified string, case insensitive.
 * - findByNameIgnoreCase(String name): Finds an ingredient by name, case insensitive.
 * <p>
 * Annotations:
 * - @Repository: Indicates that this interface is a Spring Data Repository.
 *
 * @Author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see ReactiveMongoRepository
 */
@Repository
public interface IngredientDao extends ReactiveMongoRepository<IngredientDto, String> {

    /**
     * Finds ingredients with names containing the specified string, case insensitive.
     *
     * @param name The string to search for within ingredient names.
     * @return A {@link Flux} emitting the matching {@link IngredientDto} entities.
     */
    Flux<IngredientDto> findByNameContainingIgnoreCase(String name);

    /**
     * Finds an ingredient by name, case insensitive.
     *
     * @param name The name of the ingredient to search for.
     * @return A {@link Mono} emitting the matching {@link IngredientDto} entity, or empty if not found.
     */
    Mono<IngredientDto> findByNameIgnoreCase(String name);
}
