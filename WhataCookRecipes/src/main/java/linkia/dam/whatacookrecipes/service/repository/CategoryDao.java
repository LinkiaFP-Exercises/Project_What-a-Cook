package linkia.dam.whatacookrecipes.service.repository;

import linkia.dam.whatacookrecipes.model.CategoryDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Repository interface for managing {@link CategoryDto} entities.
 * Extends the {@link ReactiveMongoRepository} to provide reactive CRUD operations.
 * <p>
 * Methods:
 * - findByNameContainingIgnoreCase(String name): Finds categories with names containing the specified string, case insensitive.
 * - findByNameIgnoreCase(String name): Finds a category by name, case insensitive.
 * <p>
 * Annotations:
 * - @Repository: Indicates that this interface is a Spring Data Repository.
 *
 * @Author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see ReactiveMongoRepository
 */
@Repository
public interface CategoryDao extends ReactiveMongoRepository<CategoryDto, String> {

    /**
     * Finds categories with names containing the specified string, case insensitive.
     *
     * @param name The string to search for within category names.
     * @return A {@link Flux} emitting the matching {@link CategoryDto} entities.
     */
    Flux<CategoryDto> findByNameContainingIgnoreCase(String name);

    /**
     * Finds a category by name, case insensitive.
     *
     * @param name The name of the category to search for.
     * @return A {@link Mono} emitting the matching {@link CategoryDto} entity, or empty if not found.
     */
    Mono<CategoryDto> findByNameIgnoreCase(String name);
}
