package linkia.dam.whatacookrecipes.service.repository;

import linkia.dam.whatacookrecipes.model.MeasureDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MeasureDao extends ReactiveMongoRepository<MeasureDto, String> {
    Mono<MeasureDto> findByNameIgnoreCase(String name);
}
