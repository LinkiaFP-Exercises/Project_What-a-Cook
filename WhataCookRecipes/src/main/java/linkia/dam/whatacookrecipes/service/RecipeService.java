package linkia.dam.whatacookrecipes.service;

import linkia.dam.whatacookrecipes.model.RecipeDto;
import linkia.dam.whatacookrecipes.model.exception.ResourceNotFoundException;
import linkia.dam.whatacookrecipes.service.components.CreateRecipesComponent;
import linkia.dam.whatacookrecipes.service.repository.RecipeDao;
import linkia.dam.whatacookrecipes.utilities.PaginationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service class for managing recipes.
 * Provides methods for CRUD operations and additional business logic.
 * <p>
 * Annotations:
 * - @AllArgsConstructor: Generates a constructor with 1 parameter for each field in the class.
 * - @Service: Indicates that this class is a Spring service component.
 * - @Slf4j: Generates a logger for the class.
 * <p>
 * Methods:
 * - getAllRecipes(int page, int size, String mode): Retrieves all recipes with pagination.
 * - getRecipesByNameContaining(String name, int page, int size, String mode): Retrieves recipes by name containing a string with pagination.
 * - findRecipesByIngredients(List<String> ingredientNames, int page, int size, String mode): Retrieves recipes containing any of the specified ingredients with pagination.
 * - findRecipesByAllIngredients(List<String> ingredientNames, int page, int size, String mode): Retrieves recipes containing all of the specified ingredients with pagination.
 * - getRecipeById(String id): Retrieves a recipe by its ID.
 * - getRecipesByIds(List<String> ids): Retrieves recipes by a list of IDs.
 * - getRecipeByName(String name): Retrieves a recipe by its name, case insensitive.
 * - createRecipes(Flux<RecipeDto> recipes): Creates multiple recipes.
 * - createRecipe(RecipeDto recipeDto): Creates a new recipe.
 * - deleteRecipe(String id): Deletes a recipe by its ID.
 * - deleteAllRecipes(): Deletes all recipes.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see RecipeDao
 * @see CreateRecipesComponent
 * @see PaginationUtil
 */
@AllArgsConstructor
@Service
@Slf4j
public class RecipeService {

    private final RecipeDao recipeDao;
    private final CreateRecipesComponent createRecipesComponent;

    /**
     * Retrieves all recipes with pagination.
     *
     * @param page The page number to retrieve.
     * @param size The number of items per page.
     * @param mode The pagination mode.
     * @return A {@link Mono} emitting a {@link Page} of {@link RecipeDto} objects.
     */
    public Mono<Page<RecipeDto>> getAllRecipes(int page, int size, String mode) {
        return recipeDao.findAll().collectList()
                .flatMap(list -> PaginationUtil.createPagedResult(list, page, size, mode));
    }

    /**
     * Retrieves recipes by name containing a string with pagination.
     *
     * @param name The string to search for within recipe names.
     * @param page The page number to retrieve.
     * @param size The number of items per page.
     * @param mode The pagination mode.
     * @return A {@link Mono} emitting a {@link Page} of {@link RecipeDto} objects.
     */
    public Mono<Page<RecipeDto>> getRecipesByNameContaining(String name, int page, int size, String mode) {
        return recipeDao.findByNameContainingIgnoreCase(name).collectList()
                .flatMap(list -> PaginationUtil.createPagedResult(list, page, size, mode));
    }

    /**
     * Retrieves recipes containing any of the specified ingredients with pagination.
     *
     * @param ingredientNames The list of ingredient names to search for within recipes.
     * @param page            The page number to retrieve.
     * @param size            The number of items per page.
     * @param mode            The pagination mode.
     * @return A {@link Mono} emitting a {@link Page} of {@link RecipeDto} objects.
     */
    public Mono<Page<RecipeDto>> findRecipesByIngredients(List<String> ingredientNames, int page, int size, String mode) {
        return recipeDao.findByIngredientsNameIn(ingredientNames).collectList()
                .flatMap(list -> PaginationUtil.createPagedResult(list, page, size, mode));
    }

    /**
     * Retrieves recipes containing all of the specified ingredients with pagination.
     *
     * @param ingredientNames The list of ingredient names to search for within recipes.
     * @param page            The page number to retrieve.
     * @param size            The number of items per page.
     * @param mode            The pagination mode.
     * @return A {@link Mono} emitting a {@link Page} of {@link RecipeDto} objects.
     */
    public Mono<Page<RecipeDto>> findRecipesByAllIngredients(List<String> ingredientNames, int page, int size, String mode) {
        return recipeDao.findByAllIngredientsNameIn(ingredientNames).collectList()
                .flatMap(list -> PaginationUtil.createPagedResult(list, page, size, mode));
    }

    /**
     * Retrieves a recipe by its ID.
     *
     * @param id The ID of the recipe to retrieve.
     * @return A {@link Mono} emitting the {@link RecipeDto} object, or an error if not found.
     */
    public Mono<RecipeDto> getRecipeById(String id) {
        return recipeDao.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Recipe not found with id=" + id)));
    }

    /**
     * Retrieves recipes by a list of IDs.
     *
     * @param ids The list of IDs of the recipes to retrieve.
     * @return A {@link Mono} emitting a map containing found recipes and not found IDs.
     */
    public Mono<Map<String, Object>> getRecipesByIds(List<String> ids) {
        return recipeDao.findAllById(ids)
                .collectList()
                .flatMap(recipes -> {
                    List<String> foundIds = recipes.stream()
                            .map(RecipeDto::getId)
                            .toList();
                    List<String> notFoundIds = ids.stream()
                            .filter(id -> !foundIds.contains(id))
                            .toList();

                    Map<String, Object> result = new HashMap<>();
                    result.put("found", recipes);
                    result.put("notFound", notFoundIds);

                    return Mono.just(result);
                });
    }

    /**
     * Retrieves a recipe by its name, case insensitive.
     *
     * @param name The name of the recipe to retrieve.
     * @return A {@link Mono} emitting the {@link RecipeDto} object, or an error if not found.
     */
    public Mono<RecipeDto> getRecipeByName(String name) {
        return recipeDao.findByNameIgnoreCase(name)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Recipe not found with name=" + name)));
    }

    /**
     * Creates multiple recipes.
     *
     * @param recipes The recipes to create.
     * @return A {@link Flux} emitting the created {@link RecipeDto} objects.
     */
    public Flux<RecipeDto> createRecipes(Flux<RecipeDto> recipes) {
        return createRecipesComponent.createRecipes(recipes);
    }

    /**
     * Creates a new recipe.
     *
     * @param recipeDto The recipe data to create.
     * @return A {@link Mono} emitting the created {@link RecipeDto} object.
     */
    public Mono<RecipeDto> createRecipe(RecipeDto recipeDto) {
        return createRecipesComponent.createRecipe(recipeDto);
    }

    /**
     * Deletes a recipe by its ID.
     *
     * @param id The ID of the recipe to delete.
     * @return A {@link Mono} emitting a message indicating the recipe has been deleted, or an error if not found.
     */
    public Mono<String> deleteRecipe(String id) {
        return recipeDao.findById(id)
                .flatMap(existingRecipe -> recipeDao.delete(existingRecipe)
                        .then(Mono.just("Recipe " + existingRecipe.getName() + " has been deleted.")))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Recipe not found with id=" + id)));
    }

    /**
     * Deletes all recipes.
     *
     * @return A {@link Mono} indicating completion of the deletion process.
     */
    public Mono<Void> deleteAllRecipes() {
        return recipeDao.deleteAll();
    }
}
