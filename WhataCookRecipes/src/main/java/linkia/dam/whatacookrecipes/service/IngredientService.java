package linkia.dam.whatacookrecipes.service;

import linkia.dam.whatacookrecipes.model.IngredientDto;
import linkia.dam.whatacookrecipes.model.exception.ResourceNotFoundException;
import linkia.dam.whatacookrecipes.service.components.CreateIngredientsComponent;
import linkia.dam.whatacookrecipes.service.repository.IngredientDao;
import linkia.dam.whatacookrecipes.utilities.PaginationUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
public class IngredientService {

    private final IngredientDao ingredientDao;
    private final CreateIngredientsComponent createIngredientsComponent;

    public Mono<Page<IngredientDto>> getAllCategories(int page, int size, String mode) {
        return ingredientDao.findAll().collectList()
                .flatMap(list -> PaginationUtil.createPagedResult(list, page, size, mode));
    }

    public Mono<Page<IngredientDto>> getCategoriesByNameContaining(String name, int page, int size, String mode) {
        return ingredientDao.findByNameContainingIgnoreCase(name).collectList()
                .flatMap(list -> PaginationUtil.createPagedResult(list, page, size, mode));
    }

    public Mono<IngredientDto> getIngredientById(String id) {
        return ingredientDao.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Ingredient not found with id=" + id)));
    }

    public Mono<Map<String, Object>> getIngredientsByIds(List<String> ids) {
        return ingredientDao.findAllById(ids)
                .collectList()
                .flatMap(ingredients -> {
                    List<String> foundIds = ingredients.stream()
                            .map(IngredientDto::getId)
                            .toList();
                    List<String> notFoundIds = ids.stream()
                            .filter(id -> !foundIds.contains(id))
                            .toList();

                    Map<String, Object> result = new HashMap<>();
                    result.put("found", ingredients);
                    result.put("notFound", notFoundIds);

                    return Mono.just(result);
                });
    }

    public Mono<IngredientDto> getIngredientByName(String name) {
        return ingredientDao.findByNameIgnoreCase(name)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Ingredient not found with name=" + name)));
    }

    public Mono<IngredientDto> createIngredient(IngredientDto ingredientDto) {
        return createIngredientsComponent.createIngredient(ingredientDto);
    }

    public Flux<IngredientDto> createCategories(Flux<IngredientDto> ingredients) {
        return createIngredientsComponent.createIngredients(ingredients);
    }

    public Mono<String> deleteIngredient(String id) {
        return ingredientDao.findById(id)
                .flatMap(existingIngredient -> ingredientDao.delete(existingIngredient)
                        .then(Mono.just("Ingredient " + existingIngredient.getName() + " has been deleted.")))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Ingredient not found with id=" + id)));
    }

    public Mono<Void> deleteAllCategories() {
        return ingredientDao.deleteAll();
    }
}
