package linkia.dam.whatacookrecipes.service.repository;

import linkia.dam.whatacookrecipes.model.RecipeDto;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface RecipeDao extends ReactiveMongoRepository<RecipeDto, String> {

    Flux<RecipeDto> findByNameContainingIgnoreCase(String name);

    Mono<RecipeDto> findByNameIgnoreCase(String name);

    @Query("{'ingredients.name': { $in: ?0 }}")
    Flux<RecipeDto> findByIngredientsNameIn(List<String> ingredientNames);

    @Query("{'ingredients.name': { $all: ?0 }}")
    Flux<RecipeDto> findByAllIngredientsNameIn(List<String> ingredientNames);
}
