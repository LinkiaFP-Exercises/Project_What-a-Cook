package linkia.dam.whatacookrecipes.service.components;

import com.mongodb.DuplicateKeyException;
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

    public static final String RETRYING_DUE_TO_DUPLICATE_KEY_ERROR = "Retrying due to duplicate key error: {}";
    private final RecipeDao recipeDao;
    private final IngredientDao ingredientDao;
    private final CategoryDao categoryDao;
    private final MeasureDao measureDao;

    public Flux<RecipeDto> createRecipes(Flux<RecipeDto> recipes) {
        return recipes.flatMap(this::createRecipe)
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
                                        .filter(throwable -> throwable instanceof DuplicateKeyException)
                                        .doBeforeRetry(CreateRecipesComponent::getWarnBy))
                )
                .flatMap(Mono::just)
                .onErrorResume(DuplicateKeyException.class, e -> {
                    log.error("Duplicate key error: {}", e.getMessage(), e);
                    return recipeDao.findByNameIgnoreCase(recipeDto.getName());
                });
    }

    private static void getWarnBy(Retry.RetrySignal retrySignal) {
        log.warn(RETRYING_DUE_TO_DUPLICATE_KEY_ERROR, retrySignal.failure().getMessage());
    }

    private Mono<RecipeDto> createIngredientsAndCategories(RecipeDto recipeDto) {
        return Flux.fromIterable(recipeDto.getIngredients())
                .flatMap(this::createOrFindIngredient)
                .collectList()
                .flatMap(savedIngredients -> {
                    recipeDto.setIngredients(savedIngredients);
                    return Flux.fromIterable(recipeDto.getCategories())
                            .flatMap(this::createOrFindCategory)
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
                                                    .filter(throwable -> throwable instanceof DuplicateKeyException)
                                                    .doBeforeRetry(CreateRecipesComponent::getWarnBy));
                                })
                )
                .flatMap(Mono::just)
                .onErrorResume(DuplicateKeyException.class, e -> {
                    log.error("Duplicate key error: {}", e.getMessage(), e);
                    return ingredientDao.findByNameIgnoreCase(ingredientDto.getName());
                });
    }

    private Mono<MeasureDto> createOrFindMeasure(MeasureDto measureDto) {
        return measureDao.findByNameIgnoreCase(measureDto.getName())
                .switchIfEmpty(
                        measureDao.save(measureDto)
                                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                                        .filter(throwable -> throwable instanceof DuplicateKeyException)
                                        .doBeforeRetry(CreateRecipesComponent::getWarnBy))
                )
                .flatMap(Mono::just)
                .onErrorResume(DuplicateKeyException.class, e -> {
                    log.error("Duplicate key error: {}", e.getMessage(), e);
                    return measureDao.findByNameIgnoreCase(measureDto.getName());
                });
    }

    private Mono<CategoryDto> createOrFindCategory(CategoryDto categoryDto) {
        return categoryDao.findByNameIgnoreCase(categoryDto.getName())
                .switchIfEmpty(
                        categoryDao.save(categoryDto)
                                .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                                        .filter(throwable -> throwable instanceof DuplicateKeyException)
                                        .doBeforeRetry(CreateRecipesComponent::getWarnBy))
                )
                .flatMap(Mono::just)
                .onErrorResume(DuplicateKeyException.class, e -> {
                    log.error("Duplicate key error: {}", e.getMessage(), e);
                    return categoryDao.findByNameIgnoreCase(categoryDto.getName());
                });
    }
}
