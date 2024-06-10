package linkia.dam.whatacookrecipies.service.contracts;

import linkia.dam.whatacookrecipies.model.MeasureDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MeasureDao extends ReactiveMongoRepository<MeasureDto, String> {
    Mono<MeasureDto> findByNameIgnoreCase(String name);
}
