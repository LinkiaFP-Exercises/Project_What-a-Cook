package linkia.dam.whatacookrecipes.service.contracts;

import linkia.dam.whatacookrecipes.model.CategoryDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CategoryDao extends ReactiveMongoRepository<CategoryDto, String> {

    Flux<CategoryDto> findByNameContainingIgnoreCase(String name);

    Mono<CategoryDto> findByNameIgnoreCase(String name);
}
