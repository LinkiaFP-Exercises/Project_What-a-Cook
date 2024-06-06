package linkia.dam.whatacookrecipies.service.contracts;

import linkia.dam.whatacookrecipies.model.RecipeDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeDao extends ReactiveMongoRepository<RecipeDto, String> {
}
