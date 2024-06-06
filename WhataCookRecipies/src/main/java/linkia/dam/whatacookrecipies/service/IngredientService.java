package linkia.dam.whatacookrecipies.service;

import linkia.dam.whatacookrecipies.model.IngredientDto;
import linkia.dam.whatacookrecipies.service.contracts.IngredientDao;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static linkia.dam.whatacookrecipies.utilities.ServiceUtil.sortByName;

@AllArgsConstructor
@Service
public class IngredientService {

    private final IngredientDao ingredientDao;

    public Flux<IngredientDto> getAllIngredients() {
        return ingredientDao.findAll();
    }

    public Flux<IngredientDto> getAllIngredients(int page, int size, String direction) {
        Pageable pageable = PageRequest.of(page, size, sortByName(direction));
        return ingredientDao.findAllBy(pageable);
    }

    public Mono<IngredientDto> getIngredientById(String id) {
        return ingredientDao.findById(id);
    }

    public Mono<IngredientDto> createIngredient(IngredientDto ingredientDto) {
        return ingredientDao.save(ingredientDto);
    }

    public Mono<IngredientDto> updateIngredient(String id, IngredientDto ingredientDto) {
        return ingredientDao.findById(id)
                .flatMap(existingIngredient -> {
                    existingIngredient.setName(ingredientDto.getName());
                    existingIngredient.setQuantity(ingredientDto.getQuantity());
                    existingIngredient.setMeasure(ingredientDto.getMeasure());
                    return ingredientDao.save(existingIngredient);
                });
    }

    public Mono<Void> deleteIngredient(String id) {
        return ingredientDao.deleteById(id);
    }


}
