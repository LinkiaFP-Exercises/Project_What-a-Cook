package linkia.dam.whatacookrecipes.controller;

import linkia.dam.whatacookrecipes.model.IngredientDto;
import linkia.dam.whatacookrecipes.service.IngredientService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing ingredients.
 * Provides endpoints for CRUD operations on ingredients.
 * <p>
 * Annotations:
 * - @AllArgsConstructor: Generates a constructor with 1 parameter for each field in the class.
 * - @RestController: Indicates that this class is a REST controller.
 * - @RequestMapping: Specifies the base URL for all endpoints in this controller.
 * - @Validated: Enables validation for the controller.
 * <p>
 * Methods:
 * - getAllCategories(String mode, int page, int size): Retrieves all ingredients with pagination.
 * - getCategoriesByNameContaining(String name, String mode, int page, int size): Retrieves ingredients by name containing a string with pagination.
 * - getIngredientById(String id): Retrieves an ingredient by its ID.
 * - getIngredientsByIds(List<String> ids): Retrieves ingredients by a list of IDs.
 * - getIngredientByName(String name): Retrieves an ingredient by its name, case insensitive.
 * - createIngredient(IngredientDto ingredientDto): Creates a new ingredient.
 * - createCategories(Flux<IngredientDto> ingredients): Creates multiple ingredients.
 * - deleteIngredientById(String id): Deletes an ingredient by its ID.
 * - deleteAllCategories(): Deletes all ingredients.
 *
 * @Author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see IngredientService
 */
@AllArgsConstructor
@RestController
@RequestMapping("${app.endpoint.ingredients}")
@Validated
public class IngredientController {

    private final IngredientService ingredientService;

    /**
     * Retrieves all ingredients with pagination.
     *
     * @param mode The sorting mode.
     * @param page The page number to retrieve.
     * @param size The number of items per page.
     * @return A {@link Mono} emitting a {@link Page} of {@link IngredientDto} objects.
     */
    @GetMapping
    public Mono<Page<IngredientDto>> getAllCategories(@RequestParam(required = false) String mode,
                                                      @RequestParam int page, @RequestParam int size) {
        return ingredientService.getAllCategories(page, size, mode);
    }

    /**
     * Retrieves ingredients by name containing a string with pagination.
     *
     * @param name The string to search for within ingredient names.
     * @param mode The sorting mode.
     * @param page The page number to retrieve.
     * @param size The number of items per page.
     * @return A {@link Mono} emitting a {@link Page} of {@link IngredientDto} objects.
     */
    @GetMapping("${app.sub-endpoint.by-name}")
    public Mono<Page<IngredientDto>> getCategoriesByNameContaining(@RequestParam String name,
                                                                   @RequestParam(required = false) String mode,
                                                                   @RequestParam int page, @RequestParam int size) {
        return ingredientService.getCategoriesByNameContaining(name, page, size, mode);
    }

    /**
     * Retrieves an ingredient by its ID.
     *
     * @param id The ID of the ingredient to retrieve.
     * @return A {@link Mono} emitting the {@link IngredientDto} object, or an error if not found.
     */
    @GetMapping("${app.sub-endpoint.id.path-variable-id}")
    public Mono<IngredientDto> getIngredientById(@PathVariable String id) {
        return ingredientService.getIngredientById(id);
    }

    /**
     * Retrieves ingredients by a list of IDs.
     *
     * @param ids The list of IDs of the ingredients to retrieve.
     * @return A {@link Mono} emitting a map containing found ingredients and not found IDs.
     */
    @PostMapping("${app.sub-endpoint.by-ids}")
    public Mono<Map<String, Object>> getIngredientsByIds(@RequestBody List<String> ids) {
        return ingredientService.getIngredientsByIds(ids);
    }

    /**
     * Retrieves an ingredient by its name, case insensitive.
     *
     * @param name The name of the ingredient to retrieve.
     * @return A {@link Mono} emitting the {@link IngredientDto} object, or an error if not found.
     */
    @GetMapping("${app.sub-endpoint.name.path-variable-name}")
    public Mono<IngredientDto> getIngredientByName(@PathVariable String name) {
        return ingredientService.getIngredientByName(name);
    }

    /**
     * Creates a new ingredient.
     *
     * @param ingredientDto The ingredient data to create.
     * @return A {@link Mono} emitting the created {@link IngredientDto} object.
     */
    @PostMapping
    public Mono<IngredientDto> createIngredient(@RequestBody IngredientDto ingredientDto) {
        return ingredientService.createIngredient(ingredientDto);
    }

    /**
     * Creates multiple ingredients.
     *
     * @param ingredients The flux of ingredients to create.
     * @return A {@link Flux} emitting the created {@link IngredientDto} objects.
     */
    @PostMapping("${app.sub-endpoint.bulk}")
    public Flux<IngredientDto> createCategories(@RequestBody Flux<IngredientDto> ingredients) {
        return ingredientService.createCategories(ingredients);
    }

    /**
     * Deletes an ingredient by its ID.
     *
     * @param id The ID of the ingredient to delete.
     * @return A {@link Mono} emitting a message indicating the ingredient has been deleted, or an error if not found.
     */
    @DeleteMapping("${app.sub-endpoint.path-variable-id}")
    public Mono<String> deleteIngredientById(@PathVariable String id) {
        return ingredientService.deleteIngredient(id);
    }

    /**
     * Deletes all ingredients.
     *
     * @return A {@link Mono} indicating completion of the deletion process.
     */
    @DeleteMapping("${app.sub-endpoint.all}")
    public Mono<Void> deleteAllCategories() {
        return ingredientService.deleteAllCategories();
    }
}
