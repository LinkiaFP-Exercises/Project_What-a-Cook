package linkia.dam.whatacookrecipes.service.components;

import com.mongodb.MongoWriteException;
import linkia.dam.whatacookrecipes.model.IngredientDto;
import linkia.dam.whatacookrecipes.service.repository.IngredientDao;
import linkia.dam.whatacookrecipes.service.MeasureService;
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
public class CreateIngredientsComponent {

    public static final String RETRYING_DUE_TO_DUPLICATE_KEY_ERROR = "Retrying due to duplicate key error for '{}': {}";
    private final IngredientDao ingredientDao;
    private final MeasureService measureService;

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

    public Mono<IngredientDto> createIngredient(IngredientDto ingredientDto) {
        return measureService.createMeasure(ingredientDto.getMeasure())
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

    private boolean isDuplicateKeyException(Throwable throwable) {
        return throwable instanceof MongoWriteException &&
                ((MongoWriteException) throwable).getCode() == 11000;
    }

    private static void getWarned(String ingredientName, Retry.RetrySignal retrySignal) {
        log.warn(RETRYING_DUE_TO_DUPLICATE_KEY_ERROR, ingredientName, retrySignal.failure().getMessage());
    }
}
