package linkia.dam.whatacookrecipies.service.contracts;

import linkia.dam.whatacookrecipies.model.IngredientDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Repository
public interface IngredientDao extends ReactiveMongoRepository<IngredientDto, String> {

    Flux<IngredientDto> findByNameContainingIgnoreCase(String name);

    Mono<IngredientDto> findByNameIgnoreCase(String name);
}
