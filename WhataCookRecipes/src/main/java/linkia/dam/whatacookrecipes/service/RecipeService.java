package linkia.dam.whatacookrecipes.service;

import linkia.dam.whatacookrecipes.model.RecipeDto;
import linkia.dam.whatacookrecipes.model.exception.ResourceNotFoundException;
import linkia.dam.whatacookrecipes.service.components.CreateRecipesComponent;
import linkia.dam.whatacookrecipes.service.repository.RecipeDao;
import linkia.dam.whatacookrecipes.utilities.PaginationUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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

    public Mono<Page<RecipeDto>> findRecipesByIngredients(List<String> ingredientNames, int page, int size, String mode) {
        return recipeDao.findByIngredientsNameIn(ingredientNames).collectList()
                .flatMap(list -> PaginationUtil.createPagedResult(list, page, size, mode));
    }

    public Mono<Page<RecipeDto>> findRecipesByAllIngredients(List<String> ingredientNames, int page, int size, String mode) {
        return recipeDao.findByAllIngredientsNameIn(ingredientNames).collectList()
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
