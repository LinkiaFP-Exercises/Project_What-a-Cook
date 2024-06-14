package linkia.dam.whatacookrecipes.service.repository;

import linkia.dam.whatacookrecipes.model.MeasureDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Repository interface for managing {@link MeasureDto} entities.
 * Extends the {@link ReactiveMongoRepository} to provide reactive CRUD operations.
 * <p>
 * Method:
 * - findByNameIgnoreCase(String name): Finds a measure by name, case insensitive.
 * <p>
 * Annotations:
 * - @Repository: Indicates that this interface is a Spring Data Repository.
 *
 * @Author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see ReactiveMongoRepository
 */
@Repository
public interface MeasureDao extends ReactiveMongoRepository<MeasureDto, String> {

    /**
     * Finds a measure by name, case insensitive.
     *
     * @param name The name of the measure to search for.
     * @return A {@link Mono} emitting the matching {@link MeasureDto} entity, or empty if not found.
     */
    Mono<MeasureDto> findByNameIgnoreCase(String name);
}
