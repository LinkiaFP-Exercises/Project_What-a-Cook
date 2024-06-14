package linkia.dam.whatacookrecipes.service.components;

import com.mongodb.MongoWriteException;
import linkia.dam.whatacookrecipes.model.IngredientDto;
import linkia.dam.whatacookrecipes.service.repository.IngredientDao;
import linkia.dam.whatacookrecipes.service.repository.MeasureDao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

/**
 * Component class for creating ingredients.
 * Provides methods to create single or multiple ingredients with proper handling of duplicate key errors.
 * <p>
 * Annotations:
 * - @AllArgsConstructor: Generates a constructor with 1 parameter for each field in the class.
 * - @Component: Indicates that this class is a Spring component.
 * - @Slf4j: Generates a logger for the class.
 * <p>
 * Methods:
 * - createIngredients(Flux<IngredientDto> ingredients): Creates multiple ingredients.
 * - createIngredient(IngredientDto ingredientDto): Creates a single ingredient with retry logic for duplicate key errors.
 * - isDuplicateKeyException(Throwable throwable): Checks if the given throwable is a duplicate key error.
 * - getWarned(String ingredientName, Retry.RetrySignal retrySignal): Logs a warning message before retrying due to duplicate key error.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see IngredientDao
 * @see MeasureDao
 */
@AllArgsConstructor
@Component
@Slf4j
public class CreateIngredientsComponent {

    public static final String RETRYING_DUE_TO_DUPLICATE_KEY_ERROR = "Retrying due to duplicate key error for '{}': {}";
    private final IngredientDao ingredientDao;
    private final MeasureDao measureDao;

    /**
     * Creates multiple ingredients.
     *
     * @param ingredients The flux of ingredients to create.
     * @return A {@link Flux} emitting the created {@link IngredientDto} objects.
     */
    public Flux<IngredientDto> createIngredients(Flux<IngredientDto> ingredients) {
        return ingredients
                .concatMap(ingredient -> Mono.just(ingredient)
//                        .delayElement(Duration.ofMillis(50)) // Introduce un retraso de 50 ms entre cada ingrediente si es necesario
                        .flatMap(this::createIngredient)
                        .onErrorResume(e -> {
                            log.error("Error occurred while processing ingredient '{}': {}", ingredient.getName(), e.getMessage(), e);
                            return Mono.empty();
                        }))
                .onErrorResume(e -> {
                    log.error("Error occurred: {}", e.getMessage(), e);
                    return Flux.empty();
                });
    }

    /**
     * Creates a single ingredient with retry logic for duplicate key errors.
     *
     * @param ingredientDto The ingredient data to create.
     * @return A {@link Mono} emitting the created {@link IngredientDto} object.
     */
    public Mono<IngredientDto> createIngredient(IngredientDto ingredientDto) {
        return measureDao.findByNameIgnoreCase(ingredientDto.getMeasure().getName())
                .switchIfEmpty(Mono.defer(() -> measureDao.save(ingredientDto.getMeasure())))
                .flatMap(measureSaved -> {
                    ingredientDto.setMeasure(measureSaved);
                    return ingredientDao.findByNameIgnoreCase(ingredientDto.getName())
                            .switchIfEmpty(Mono.defer(() -> ingredientDao.save(ingredientDto))
                                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
                                            .filter(this::isDuplicateKeyException)
                                            .doBeforeRetry(retrySignal -> getWarned(ingredientDto.getName(), retrySignal))));
                })
                .flatMap(Mono::just)
                .onErrorResume(MongoWriteException.class, e -> {
                    log.error("Duplicate key error for ingredient '{}': {}", ingredientDto.getName(), e.getMessage(), e);
                    return ingredientDao.findByNameIgnoreCase(ingredientDto.getName());
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
     * @param ingredientName The name of the ingredient being retried.
     * @param retrySignal    The retry signal containing failure information.
     */
    private static void getWarned(String ingredientName, Retry.RetrySignal retrySignal) {
        log.warn(RETRYING_DUE_TO_DUPLICATE_KEY_ERROR, ingredientName, retrySignal.failure().getMessage());
    }
}
