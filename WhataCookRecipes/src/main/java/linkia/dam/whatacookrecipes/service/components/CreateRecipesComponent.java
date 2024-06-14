package linkia.dam.whatacookrecipes.service.components;

import com.mongodb.MongoWriteException;
import linkia.dam.whatacookrecipes.model.CategoryDto;
import linkia.dam.whatacookrecipes.model.IngredientDto;
import linkia.dam.whatacookrecipes.model.MeasureDto;
import linkia.dam.whatacookrecipes.model.RecipeDto;
import linkia.dam.whatacookrecipes.service.repository.CategoryDao;
import linkia.dam.whatacookrecipes.service.repository.IngredientDao;
import linkia.dam.whatacookrecipes.service.repository.MeasureDao;
import linkia.dam.whatacookrecipes.service.repository.RecipeDao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * Component class for creating recipes.
 * Provides methods to create single or multiple recipes with proper handling of duplicate key errors.
 * <p>
 * Annotations:
 * - @AllArgsConstructor: Generates a constructor with 1 parameter for each field in the class.
 * - @Component: Indicates that this class is a Spring component.
 * - @Slf4j: Generates a logger for the class.
 * <p>
 * Methods:
 * - createRecipes(Flux<RecipeDto> recipes): Creates multiple recipes.
 * - createRecipe(RecipeDto recipeDto): Creates a single recipe with retry logic for duplicate key errors.
 * - createIngredientsAndCategories(RecipeDto recipeDto): Creates or finds ingredients and categories for a recipe.
 * - createOrFindIngredient(IngredientDto ingredientDto): Creates or finds a single ingredient.
 * - createOrFindMeasure(MeasureDto measureDto): Creates or finds a single measure.
 * - createOrFindCategory(CategoryDto categoryDto): Creates or finds a single category.
 * - isDuplicateKeyException(Throwable throwable): Checks if the given throwable is a duplicate key error.
 * - getWarned(String recipeDto, Retry.RetrySignal retrySignal): Logs a warning message before retrying due to duplicate key error.
 *
 * @Author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see RecipeDao
 * @see IngredientDao
 * @see CategoryDao
 * @see MeasureDao
 */
@AllArgsConstructor
@Component
@Slf4j
public class CreateRecipesComponent {

    public static final String RETRYING_DUE_TO_DUPLICATE_KEY_ERROR = "Retrying due to duplicate key error for '{}': {}";
    private final RecipeDao recipeDao;
    private final IngredientDao ingredientDao;
    private final CategoryDao categoryDao;
    private final MeasureDao measureDao;

    /**
     * Creates multiple recipes.
     *
     * @param recipes The flux of recipes to create.
     * @return A {@link Flux} emitting the created {@link RecipeDto} objects.
     */
    public Flux<RecipeDto> createRecipes(Flux<RecipeDto> recipes) {
        return recipes
                .concatMap(recipe -> Mono.just(recipe)
                        // .delayElement(Duration.ofMillis(50)) // Introduce a delay of 50 ms between each recipe if necessary to avoid race conditions
                        .flatMap(this::createRecipe)
                        .onErrorResume(e -> {
                            log.error("Error occurred while processing recipe '{}': {}", recipe.getName(), e.getMessage(), e);
                            return Mono.empty();
                        }))
                .onErrorResume(e -> {
                    log.error("Error occurred: {}", e.getMessage(), e);
                    return Flux.empty();
                });
    }

    /**
     * Creates a single recipe with retry logic for duplicate key errors.
     *
     * @param recipeDto The recipe data to create.
     * @return A {@link Mono} emitting the created {@link RecipeDto} object.
     */
    public Mono<RecipeDto> createRecipe(RecipeDto recipeDto) {
        return recipeDao.findByNameIgnoreCase(recipeDto.getName())
                .switchIfEmpty(
                        createIngredientsAndCategories(recipeDto)
                                .flatMap(saved -> recipeDao.save(recipeDto))
                                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                                        .filter(this::isDuplicateKeyException)
                                        .doBeforeRetry(retrySignal -> getWarned(recipeDto.getName(), retrySignal)))
                )
                .flatMap(Mono::just)
                .onErrorResume(MongoWriteException.class, e -> {
                    log.error("Duplicate key error for recipe '{}': {}", recipeDto.getName(), e.getMessage(), e);
                    return recipeDao.findByNameIgnoreCase(recipeDto.getName());
                });
    }

    /**
     * Creates or finds ingredients and categories for a recipe.
     *
     * @param recipeDto The recipe data to process.
     * @return A {@link Mono} emitting the {@link RecipeDto} object with saved ingredients and categories.
     */
    private Mono<RecipeDto> createIngredientsAndCategories(RecipeDto recipeDto) {
        return Flux.fromIterable(recipeDto.getIngredients())
                .concatMap(this::createOrFindIngredient)
                .collectList()
                .flatMap(savedIngredients -> {
                    recipeDto.setIngredients(savedIngredients);
                    return Flux.fromIterable(recipeDto.getCategories())
                            .concatMap(this::createOrFindCategory)
                            .collectList();
                })
                .flatMap(savedCategories -> {
                    recipeDto.setCategories(savedCategories);
                    return Mono.just(recipeDto);
                });
    }

    /**
     * Creates or finds a single ingredient.
     *
     * @param ingredientDto The ingredient data to process.
     * @return A {@link Mono} emitting the {@link IngredientDto} object.
     */
    private Mono<IngredientDto> createOrFindIngredient(IngredientDto ingredientDto) {
        return ingredientDao.findByNameIgnoreCase(ingredientDto.getName())
                .switchIfEmpty(
                        createOrFindMeasure(ingredientDto.getMeasure())
                                .flatMap(measureSaved -> {
                                    ingredientDto.setMeasure(measureSaved);
                                    return ingredientDao.save(ingredientDto)
                                            .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                                                    .filter(this::isDuplicateKeyException)
                                                    .doBeforeRetry(retrySignal -> getWarned(ingredientDto.getName(), retrySignal)));
                                })
                )
                .flatMap(Mono::just)
                .onErrorResume(MongoWriteException.class, e -> {
                    log.error("Duplicate key error for ingredient '{}': {}", ingredientDto.getName(), e.getMessage(), e);
                    return ingredientDao.findByNameIgnoreCase(ingredientDto.getName());
                });
    }

    /**
     * Creates or finds a single measure.
     *
     * @param measureDto The measure data to process.
     * @return A {@link Mono} emitting the {@link MeasureDto} object.
     */
    private Mono<MeasureDto> createOrFindMeasure(MeasureDto measureDto) {
        return measureDao.findByNameIgnoreCase(measureDto.getName())
                .switchIfEmpty(
                        measureDao.save(measureDto)
                                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                                        .filter(this::isDuplicateKeyException)
                                        .doBeforeRetry(retrySignal -> getWarned(measureDto.getName(), retrySignal)))
                )
                .flatMap(Mono::just)
                .onErrorResume(MongoWriteException.class, e -> {
                    log.error("Duplicate key error for measure '{}': {}", measureDto.getName(), e.getMessage(), e);
                    return measureDao.findByNameIgnoreCase(measureDto.getName());
                });
    }

    /**
     * Creates or finds a single category.
     *
     * @param categoryDto The category data to process.
     * @return A {@link Mono} emitting the {@link CategoryDto} object.
     */
    private Mono<CategoryDto> createOrFindCategory(CategoryDto categoryDto) {
        return categoryDao.findByNameIgnoreCase(categoryDto.getName())
                .switchIfEmpty(
                        categoryDao.save(categoryDto)
                                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                                        .filter(this::isDuplicateKeyException)
                                        .doBeforeRetry(retrySignal -> getWarned(categoryDto.getName(), retrySignal)))
                )
                .flatMap(Mono::just)
                .onErrorResume(MongoWriteException.class, e -> {
                    log.error("Duplicate key error for category '{}': {}", categoryDto.getName(), e.getMessage(), e);
                    return categoryDao.findByNameIgnoreCase(categoryDto.getName());
                });
    }

    /**
     * Checks if the given throwable is a duplicate key error.
     *
     * @param throwable The throwable to check.
     * @return true if the throwable is a duplicate key error, false otherwise.
     */
    private boolean isDuplicateKeyException(Throwable throwable) {
        return throwable instanceof MongoWriteException &&
                ((MongoWriteException) throwable).getCode() == 11000;
    }

    /**
     * Logs a warning message before retrying due to duplicate key error.
     *
     * @param recipeDto   The name of the recipe being retried.
     * @param retrySignal The retry signal containing failure information.
     */
    private static void getWarned(String recipeDto, Retry.RetrySignal retrySignal) {
        log.warn(RETRYING_DUE_TO_DUPLICATE_KEY_ERROR, recipeDto, retrySignal.failure().getMessage());
    }

}
