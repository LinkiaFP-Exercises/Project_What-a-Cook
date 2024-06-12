package linkia.dam.whatacookrecipes.service.components;

import com.mongodb.MongoWriteException;
import linkia.dam.whatacookrecipes.model.CategoryDto;
import linkia.dam.whatacookrecipes.model.IngredientDto;
import linkia.dam.whatacookrecipes.model.MeasureDto;
import linkia.dam.whatacookrecipes.model.RecipeDto;
import linkia.dam.whatacookrecipes.service.contracts.CategoryDao;
import linkia.dam.whatacookrecipes.service.contracts.IngredientDao;
import linkia.dam.whatacookrecipes.service.contracts.MeasureDao;
import linkia.dam.whatacookrecipes.service.contracts.RecipeDao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@AllArgsConstructor
@Component
@Slf4j
public class CreateRecipesComponent {

    public static final String RETRYING_DUE_TO_DUPLICATE_KEY_ERROR = "Retrying due to duplicate key error for '{}': {}";
    private final RecipeDao recipeDao;
    private final IngredientDao ingredientDao;
    private final CategoryDao categoryDao;
    private final MeasureDao measureDao;

    public Flux<RecipeDto> createRecipes(Flux<RecipeDto> recipes) {
        return recipes
                .concatMap(recipe -> Mono.just(recipe)
//                        .delayElement(Duration.ofMillis(50)) // Introduce un retraso de 50 ms entre cada receta evitar CONDICIÓN DE CARRERA
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

    private boolean isDuplicateKeyException(Throwable throwable) {
        return throwable instanceof MongoWriteException &&
                ((MongoWriteException) throwable).getCode() == 11000;
    }

    private static void getWarned(String recipeDto, Retry.RetrySignal retrySignal) {
        log.warn(RETRYING_DUE_TO_DUPLICATE_KEY_ERROR, recipeDto, retrySignal.failure().getMessage());
    }

}
