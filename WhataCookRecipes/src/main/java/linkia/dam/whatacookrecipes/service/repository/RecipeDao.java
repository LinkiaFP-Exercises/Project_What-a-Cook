package linkia.dam.whatacookrecipes.service.repository;

import linkia.dam.whatacookrecipes.model.RecipeDto;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Repository interface for managing {@link RecipeDto} entities.
 * Extends the {@link ReactiveMongoRepository} to provide reactive CRUD operations.
 * <p>
 * Methods:
 * - findByNameContainingIgnoreCase(String name): Finds recipes with names containing the specified string, case insensitive.
 * - findByNameIgnoreCase(String name): Finds a recipe by name, case insensitive.
 * - findByIngredientsNameIn(List<String> ingredientNames): Finds recipes containing any of the specified ingredient names.
 * - findByAllIngredientsNameIn(List<String> ingredientNames): Finds recipes containing all of the specified ingredient names.
 * <p>
 * Annotations:
 * - @Repository: Indicates that this interface is a Spring Data Repository.
 * - @Query: Custom query for finding recipes by ingredient names.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see ReactiveMongoRepository
 */
@Repository
public interface RecipeDao extends ReactiveMongoRepository<RecipeDto, String> {

    /**
     * Finds recipes with names containing the specified string, case insensitive.
     *
     * @param name The string to search for within recipe names.
     * @return A {@link Flux} emitting the matching {@link RecipeDto} entities.
     */
    Flux<RecipeDto> findByNameContainingIgnoreCase(String name);

    /**
     * Finds a recipe by name, case insensitive.
     *
     * @param name The name of the recipe to search for.
     * @return A {@link Mono} emitting the matching {@link RecipeDto} entity, or empty if not found.
     */
    Mono<RecipeDto> findByNameIgnoreCase(String name);

    /**
     * Finds recipes containing any of the specified ingredient names.
     *
     * @param ingredientNames A list of ingredient names to search for within recipes.
     * @return A {@link Flux} emitting the matching {@link RecipeDto} entities.
     */
    @Query("{'ingredients.name': { $in: ?0 }}")
    Flux<RecipeDto> findByIngredientsNameIn(List<String> ingredientNames);

    /**
     * Finds recipes containing all of the specified ingredient names.
     *
     * @param ingredientNames A list of ingredient names to search for within recipes.
     * @return A {@link Flux} emitting the matching {@link RecipeDto} entities.
     */
    @Query("{'ingredients.name': { $all: ?0 }}")
    Flux<RecipeDto> findByAllIngredientsNameIn(List<String> ingredientNames);
}
