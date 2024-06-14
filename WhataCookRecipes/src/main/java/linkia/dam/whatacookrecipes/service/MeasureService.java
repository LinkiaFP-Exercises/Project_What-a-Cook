package linkia.dam.whatacookrecipes.service;

import linkia.dam.whatacookrecipes.model.MeasureDto;
import linkia.dam.whatacookrecipes.model.exception.ResourceNotFoundException;
import linkia.dam.whatacookrecipes.service.repository.MeasureDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service class for managing measures.
 * Provides methods for CRUD operations and additional business logic.
 * <p>
 * Annotations:
 * - @AllArgsConstructor: Generates a constructor with 1 parameter for each field in the class.
 * - @Service: Indicates that this class is a Spring service component.
 * <p>
 * Methods:
 * - getMeasureById(String id): Retrieves a measure by its ID.
 * - getMeasureByNameIgnoreCase(String name): Retrieves a measure by its name, case insensitive.
 * - createMeasure(MeasureDto measureDto): Creates a new measure if it does not already exist.
 * - createMeasures(Flux<MeasureDto> measures): Creates multiple measures.
 * - deleteMeasure(String id): Deletes a measure by its ID.
 * - deleteAllMeasures(): Deletes all measures.
 *
 * @Author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see MeasureDao
 */
@AllArgsConstructor
@Service
public class MeasureService {

    private final MeasureDao measureDao;

    /**
     * Retrieves a measure by its ID.
     *
     * @param id The ID of the measure to retrieve.
     * @return A {@link Mono} emitting the {@link MeasureDto} object, or an error if not found.
     */
    public Mono<MeasureDto> getMeasureById(String id) {
        return measureDao.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Measure not found with id=" + id)));
    }

    /**
     * Retrieves a measure by its name, case insensitive.
     *
     * @param name The name of the measure to retrieve.
     * @return A {@link Mono} emitting the {@link MeasureDto} object, or an error if not found.
     */
    public Mono<MeasureDto> getMeasureByNameIgnoreCase(String name) {
        return measureDao.findByNameIgnoreCase(name)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Measure not found with name=" + name)));
    }

    /**
     * Creates a new measure if it does not already exist.
     *
     * @param measureDto The measure data to create.
     * @return A {@link Mono} emitting the created {@link MeasureDto} object.
     */
    public Mono<MeasureDto> createMeasure(MeasureDto measureDto) {
        return measureDao.findByNameIgnoreCase(measureDto.getName())
                .switchIfEmpty(Mono.defer(() -> measureDao.save(measureDto)));
    }

    /**
     * Creates multiple measures.
     *
     * @param measures The measures to create.
     * @return A {@link Flux} emitting the created {@link MeasureDto} objects.
     */
    public Flux<MeasureDto> createMeasures(Flux<MeasureDto> measures) {
        return measures.flatMap(this::createMeasure);
    }

    /**
     * Deletes a measure by its ID.
     *
     * @param id The ID of the measure to delete.
     * @return A {@link Mono} emitting a message indicating the measure has been deleted, or an error if not found.
     */
    public Mono<String> deleteMeasure(String id) {
        return measureDao.findById(id)
                .flatMap(existingMeasure -> measureDao.delete(existingMeasure)
                        .then(Mono.just("Measure " + existingMeasure.getName() + " has been deleted.")))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Measure not found with id=" + id)));
    }

    /**
     * Deletes all measures.
     *
     * @return A {@link Mono} indicating completion of the deletion process.
     */
    public Mono<Void> deleteAllMeasures() {
        return measureDao.deleteAll();
    }

}
