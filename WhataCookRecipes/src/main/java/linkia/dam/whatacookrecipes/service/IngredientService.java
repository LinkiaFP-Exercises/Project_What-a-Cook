package linkia.dam.whatacookrecipes.service;

import linkia.dam.whatacookrecipes.model.IngredientDto;
import linkia.dam.whatacookrecipes.model.exception.ResourceNotFoundException;
import linkia.dam.whatacookrecipes.service.components.CreateIngredientsComponent;
import linkia.dam.whatacookrecipes.service.repository.IngredientDao;
import linkia.dam.whatacookrecipes.utilities.PaginationUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for managing ingredients.
 * Provides methods for CRUD operations and additional business logic.
 * <p>
 * Annotations:
 * - @AllArgsConstructor: Generates a constructor with 1 parameter for each field in the class.
 * - @Service: Indicates that this class is a Spring service component.
 * <p>
 * Methods:
 * - getAllCategories(int page, int size, String mode): Retrieves all ingredients with pagination.
 * - getCategoriesByNameContaining(String name, int page, int size, String mode): Retrieves ingredients by name containing a string with pagination.
 * - getIngredientById(String id): Retrieves an ingredient by its ID.
 * - getIngredientsByIds(List<String> ids): Retrieves ingredients by a list of IDs.
 * - getIngredientByName(String name): Retrieves an ingredient by its name, case insensitive.
 * - createIngredient(IngredientDto ingredientDto): Creates a new ingredient.
 * - createCategories(Flux<IngredientDto> ingredients): Creates multiple ingredients.
 * - deleteIngredient(String id): Deletes an ingredient by its ID.
 * - deleteAllCategories(): Deletes all ingredients.
 *
 * @Author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see IngredientDao
 * @see CreateIngredientsComponent
 * @see PaginationUtil
 */
@AllArgsConstructor
@Service
public class IngredientService {

    private final IngredientDao ingredientDao;
    private final CreateIngredientsComponent createIngredientsComponent;

    /**
     * Retrieves all ingredients with pagination.
     *
     * @param page The page number to retrieve.
     * @param size The number of items per page.
     * @param mode The pagination mode.
     * @return A {@link Mono} emitting a {@link Page} of {@link IngredientDto} objects.
     */
    public Mono<Page<IngredientDto>> getAllCategories(int page, int size, String mode) {
        return ingredientDao.findAll().collectList()
                .flatMap(list -> PaginationUtil.createPagedResult(list, page, size, mode));
    }

    /**
     * Retrieves ingredients by name containing a string with pagination.
     *
     * @param name The string to search for within ingredient names.
     * @param page The page number to retrieve.
     * @param size The number of items per page.
     * @param mode The pagination mode.
     * @return A {@link Mono} emitting a {@link Page} of {@link IngredientDto} objects.
     */
    public Mono<Page<IngredientDto>> getCategoriesByNameContaining(String name, int page, int size, String mode) {
        return ingredientDao.findByNameContainingIgnoreCase(name).collectList()
                .flatMap(list -> PaginationUtil.createPagedResult(list, page, size, mode));
    }

    /**
     * Retrieves an ingredient by its ID.
     *
     * @param id The ID of the ingredient to retrieve.
     * @return A {@link Mono} emitting the {@link IngredientDto} object, or an error if not found.
     */
    public Mono<IngredientDto> getIngredientById(String id) {
        return ingredientDao.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Ingredient not found with id=" + id)));
    }

    /**
     * Retrieves ingredients by a list of IDs.
     *
     * @param ids The list of IDs of the ingredients to retrieve.
     * @return A {@link Mono} emitting a map containing found ingredients and not found IDs.
     */
    public Mono<Map<String, Object>> getIngredientsByIds(List<String> ids) {
        return ingredientDao.findAllById(ids)
                .collectList()
                .flatMap(ingredients -> {
                    List<String> foundIds = ingredients.stream()
                            .map(IngredientDto::getId)
                            .toList();
                    List<String> notFoundIds = ids.stream()
                            .filter(id -> !foundIds.contains(id))
                            .toList();

                    Map<String, Object> result = new HashMap<>();
                    result.put("found", ingredients);
                    result.put("notFound", notFoundIds);

                    return Mono.just(result);
                });
    }

    /**
     * Retrieves an ingredient by its name, case insensitive.
     *
     * @param name The name of the ingredient to retrieve.
     * @return A {@link Mono} emitting the {@link IngredientDto} object, or an error if not found.
     */
    public Mono<IngredientDto> getIngredientByName(String name) {
        return ingredientDao.findByNameIgnoreCase(name)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Ingredient not found with name=" + name)));
    }

    /**
     * Creates a new ingredient.
     *
     * @param ingredientDto The ingredient data to create.
     * @return A {@link Mono} emitting the created {@link IngredientDto} object.
     */
    public Mono<IngredientDto> createIngredient(IngredientDto ingredientDto) {
        return createIngredientsComponent.createIngredient(ingredientDto);
    }

    /**
     * Creates multiple ingredients.
     *
     * @param ingredients The ingredients to create.
     * @return A {@link Flux} emitting the created {@link IngredientDto} objects.
     */
    public Flux<IngredientDto> createCategories(Flux<IngredientDto> ingredients) {
        return createIngredientsComponent.createIngredients(ingredients);
    }

    /**
     * Deletes an ingredient by its ID.
     *
     * @param id The ID of the ingredient to delete.
     * @return A {@link Mono} emitting a message indicating the ingredient has been deleted, or an error if not found.
     */
    public Mono<String> deleteIngredient(String id) {
        return ingredientDao.findById(id)
                .flatMap(existingIngredient -> ingredientDao.delete(existingIngredient)
                        .then(Mono.just("Ingredient " + existingIngredient.getName() + " has been deleted.")))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Ingredient not found with id=" + id)));
    }

    /**
     * Deletes all ingredients.
     *
     * @return A {@link Mono} indicating completion of the deletion process.
     */
    public Mono<Void> deleteAllCategories() {
        return ingredientDao.deleteAll();
    }
}
