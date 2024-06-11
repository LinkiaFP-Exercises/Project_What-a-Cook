package linkia.dam.whatacookrecipes.service.contracts;

import linkia.dam.whatacookrecipes.model.RecipeDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface RecipeDao extends ReactiveMongoRepository<RecipeDto, String> {

    Flux<RecipeDto> findByNameContainingIgnoreCase(String name);

    Mono<RecipeDto> findByNameIgnoreCase(String name);
}
