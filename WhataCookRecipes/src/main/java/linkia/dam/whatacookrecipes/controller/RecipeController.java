package linkia.dam.whatacookrecipes.controller;

import linkia.dam.whatacookrecipes.model.RecipeDto;
import linkia.dam.whatacookrecipes.service.RecipeService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing recipes.
 * Provides endpoints for CRUD operations on recipes.
 * <p>
 * Annotations:
 * - @AllArgsConstructor: Generates a constructor with 1 parameter for each field in the class.
 * - @RestController: Indicates that this class is a REST controller.
 * - @RequestMapping: Specifies the base URL for all endpoints in this controller.
 * - @Validated: Enables validation for the controller.
 * <p>
 * Methods:
 * - getAllRecipes(String mode, int page, int size): Retrieves all recipes with pagination.
 * - getRecipesByNameContaining(String name, String mode, int page, int size): Retrieves recipes by name containing a string with pagination.
 * - searchRecipesByIngredients(List<String> ingredients, String mode, int page, int size): Retrieves recipes containing any of the specified ingredients with pagination.
 * - searchRecipesByAllIngredients(List<String> ingredients, String mode, int page, int size): Retrieves recipes containing all of the specified ingredients with pagination.
 * - getRecipeById(String id): Retrieves a recipe by its ID.
 * - getRecipesByIds(List<String> ids): Retrieves recipes by a list of IDs.
 * - getRecipeByName(String name): Retrieves a recipe by its name, case insensitive.
 * - createRecipe(RecipeDto recipeDto): Creates a new recipe.
 * - createRecipes(Flux<RecipeDto> recipes): Creates multiple recipes.
 * - deleteRecipeById(String id): Deletes a recipe by its ID.
 * - deleteAllRecipes(): Deletes all recipes.
 *
 * @Author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see RecipeService
 */
@AllArgsConstructor
@RestController
@RequestMapping("${app.endpoint.recipes}")
@Validated
public class RecipeController {

    private final RecipeService recipeService;

    /**
     * Retrieves all recipes with pagination.
     *
     * @param mode The sorting mode.
     * @param page The page number to retrieve.
     * @param size The number of items per page.
     * @return A {@link Mono} emitting a {@link Page} of {@link RecipeDto} objects.
     */
    @GetMapping
    public Mono<Page<RecipeDto>> getAllRecipes(@RequestParam(required = false) String mode,
                                               @RequestParam int page, @RequestParam int size) {
        return recipeService.getAllRecipes(page, size, mode);
    }

    /**
     * Retrieves recipes by name containing a string with pagination.
     *
     * @param name The string to search for within recipe names.
     * @param mode The sorting mode.
     * @param page The page number to retrieve.
     * @param size The number of items per page.
     * @return A {@link Mono} emitting a {@link Page} of {@link RecipeDto} objects.
     */
    @GetMapping("${app.sub-endpoint.by-name}")
    public Mono<Page<RecipeDto>> getRecipesByNameContaining(@RequestParam String name,
                                                            @RequestParam(required = false) String mode,
                                                            @RequestParam int page, @RequestParam int size) {
        return recipeService.getRecipesByNameContaining(name, page, size, mode);
    }

    /**
     * Retrieves recipes containing any of the specified ingredients with pagination.
     *
     * @param ingredients The list of ingredient names to search for within recipes.
     * @param mode        The sorting mode.
     * @param page        The page number to retrieve.
     * @param size        The number of items per page.
     * @return A {@link Mono} emitting a {@link Page} of {@link RecipeDto} objects.
     */
    @GetMapping("${app.sub-endpoint.by-ingredients}")
    public Mono<Page<RecipeDto>> searchRecipesByIngredients(@RequestParam List<String> ingredients,
                                                            @RequestParam(required = false) String mode,
                                                            @RequestParam int page, @RequestParam int size) {
        return recipeService.findRecipesByIngredients(ingredients, page, size, mode);
    }

    /**
     * Retrieves recipes containing all of the specified ingredients with pagination.
     *
     * @param ingredients The list of ingredient names to search for within recipes.
     * @param mode        The sorting mode.
     * @param page        The page number to retrieve.
     * @param size        The number of items per page.
     * @return A {@link Mono} emitting a {@link Page} of {@link RecipeDto} objects.
     */
    @GetMapping("${app.sub-endpoint.by-all-ingredients}")
    public Mono<Page<RecipeDto>> searchRecipesByAllIngredients(@RequestParam List<String> ingredients,
                                                               @RequestParam(required = false) String mode,
                                                               @RequestParam int page, @RequestParam int size) {
        return recipeService.findRecipesByAllIngredients(ingredients, page, size, mode);
    }

    /**
     * Retrieves a recipe by its ID.
     *
     * @param id The ID of the recipe to retrieve.
     * @return A {@link Mono} emitting the {@link RecipeDto} object, or an error if not found.
     */
    @GetMapping("${app.sub-endpoint.id.path-variable-id}")
    public Mono<RecipeDto> getRecipeById(@PathVariable String id) {
        return recipeService.getRecipeById(id);
    }

    /**
     * Retrieves recipes by a list of IDs.
     *
     * @param ids The list of IDs of the recipes to retrieve.
     * @return A {@link Mono} emitting a map containing found recipes and not found IDs.
     */
    @PostMapping("${app.sub-endpoint.by-ids}")
    public Mono<Map<String, Object>> getRecipesByIds(@RequestBody List<String> ids) {
        return recipeService.getRecipesByIds(ids);
    }

    /**
     * Retrieves a recipe by its name, case insensitive.
     *
     * @param name The name of the recipe to retrieve.
     * @return A {@link Mono} emitting the {@link RecipeDto} object, or an error if not found.
     */
    @GetMapping("${app.sub-endpoint.name.path-variable-name}")
    public Mono<RecipeDto> getRecipeByName(@PathVariable String name) {
        return recipeService.getRecipeByName(name);
    }

    /**
     * Creates a new recipe.
     *
     * @param recipeDto The recipe data to create.
     * @return A {@link Mono} emitting the created {@link RecipeDto} object.
     */
    @PostMapping
    public Mono<RecipeDto> createRecipe(@RequestBody RecipeDto recipeDto) {
        return recipeService.createRecipe(recipeDto);
    }

    /**
     * Creates multiple recipes.
     *
     * @param recipes The flux of recipes to create.
     * @return A {@link Flux} emitting the created {@link RecipeDto} objects.
     */
    @PostMapping("${app.sub-endpoint.bulk}")
    public Flux<RecipeDto> createRecipes(@RequestBody Flux<RecipeDto> recipes) {
        return recipeService.createRecipes(recipes);
    }

    /**
     * Deletes a recipe by its ID.
     *
     * @param id The ID of the recipe to delete.
     * @return A {@link Mono} emitting a message indicating the recipe has been deleted, or an error if not found.
     */
    @DeleteMapping("${app.sub-endpoint.path-variable-id}")
    public Mono<String> deleteRecipeById(@PathVariable String id) {
        return recipeService.deleteRecipe(id);
    }

    /**
     * Deletes all recipes.
     *
     * @return A {@link Mono} indicating completion of the deletion process.
     */
    @DeleteMapping("${app.sub-endpoint.all}")
    public Mono<Void> deleteAllRecipes() {
        return recipeService.deleteAllRecipes();
    }

}
