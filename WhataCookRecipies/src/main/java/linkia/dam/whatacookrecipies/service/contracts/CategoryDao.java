package linkia.dam.whatacookrecipies.service.contracts;

import linkia.dam.whatacookrecipies.model.CategoryDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface CategoryDao extends ReactiveMongoRepository<CategoryDto, String> {
    Flux<CategoryDto> findAllBy(Pageable pageable);
    Flux<CategoryDto> findByNameContainingIgnoreCase(String name);
    Flux<CategoryDto> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
