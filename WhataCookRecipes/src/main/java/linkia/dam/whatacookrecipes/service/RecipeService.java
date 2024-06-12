package linkia.dam.whatacookrecipes.service;

import com.mongodb.DuplicateKeyException;
import linkia.dam.whatacookrecipes.model.CategoryDto;
import linkia.dam.whatacookrecipes.model.IngredientDto;
import linkia.dam.whatacookrecipes.model.MeasureDto;
import linkia.dam.whatacookrecipes.model.RecipeDto;
import linkia.dam.whatacookrecipes.model.exception.ResourceNotFoundException;
import linkia.dam.whatacookrecipes.service.components.CreateRecipesComponent;
import linkia.dam.whatacookrecipes.service.contracts.CategoryDao;
import linkia.dam.whatacookrecipes.service.contracts.IngredientDao;
import linkia.dam.whatacookrecipes.service.contracts.MeasureDao;
import linkia.dam.whatacookrecipes.service.contracts.RecipeDao;
import linkia.dam.whatacookrecipes.utilities.PaginationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
@Slf4j
public class RecipeService {

    private final RecipeDao recipeDao;
    private final CreateRecipesComponent createRecipesComponent;


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

    public Flux<RecipeDto> createRecipes(Flux<RecipeDto> recipes) {
        return createRecipesComponent.createRecipes(recipes);
    }

    public Mono<RecipeDto> createRecipe(RecipeDto recipeDto) {
        return createRecipesComponent.createRecipe(recipeDto);
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
