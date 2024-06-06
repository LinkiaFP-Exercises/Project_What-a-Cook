package linkia.dam.whatacookrecipies.service.contracts;

import linkia.dam.whatacookrecipies.model.MeasureDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasureDao extends ReactiveMongoRepository<MeasureDto, String> {
}
