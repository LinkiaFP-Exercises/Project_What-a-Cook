package linkia.dam.whatacookrecipies.service;

import linkia.dam.whatacookrecipies.model.RecipeDto;
import linkia.dam.whatacookrecipies.model.exception.ResourceNotFoundException;
import linkia.dam.whatacookrecipies.service.contracts.RecipeDao;
import linkia.dam.whatacookrecipies.utilities.PaginationUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class RecipeService {

    private final RecipeDao recipeDao;

    public Mono<Page<RecipeDto>> getAllRecipes(int page, int size, String mode) {
        return recipeDao.findAll().collectList()
                .flatMap(list -> PaginationUtil.createPagedResult(list, page, size, mode));
    }

    public Mono<Page<RecipeDto>> getRecipesByNameContaining(String name, int page, int size, String mode) {
        return recipeDao.findByNameContainingIgnoreCase(name).collectList()
                .flatMap(list -> PaginationUtil.createPagedResult(list, page, size, mode));
    }

    public Mono<RecipeDto> getRecipeById(String id) {
        return recipeDao.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Recipe not found with id=" + id)));
    }
    public Mono<RecipeDto> getRecipeByName(String name) {
        return recipeDao.findByNameIgnoreCase(name)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Recipe not found with name=" + name)));
    }

    public Mono<RecipeDto> createRecipe(RecipeDto recipeDto) {
        return recipeDao.findByNameIgnoreCase(recipeDto.getName())
                .switchIfEmpty(Mono.defer(() -> recipeDao.save(recipeDto)));
    }

    public Flux<RecipeDto> createRecipes(Flux<RecipeDto> recipes) {
        return recipes.flatMap(this::createRecipe);
    }

    public Mono<String> deleteRecipe(String id) {
        return recipeDao.findById(id)
                .flatMap(existingRecipe -> recipeDao.delete(existingRecipe)
                        .then(Mono.just("Recipe " + existingRecipe.getName() + " has been deleted.")))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Recipe not found with id=" + id)));
    }

    public Mono<Void> deleteAllRecipes() {
        return recipeDao.deleteAll();
    }

}
