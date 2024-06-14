package linkia.dam.whatacookrecipes.controller;

import linkia.dam.whatacookrecipes.model.RecipeDto;
import linkia.dam.whatacookrecipes.service.RecipeService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("${app.endpoint.recipes}")
@Validated
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping
    public Mono<Page<RecipeDto>> getAllRecipes(@RequestParam(required = false) String mode,
                                                    @RequestParam int page, @RequestParam int size) {
        return recipeService.getAllRecipes(page, size, mode);
    }

    @GetMapping("${app.sub-endpoint.by-name}")
    public Mono<Page<RecipeDto>> getRecipesByNameContaining(@RequestParam String name,
                                                                 @RequestParam(required = false) String mode,
                                                                 @RequestParam int page, @RequestParam int size) {
        return recipeService.getRecipesByNameContaining(name, page, size, mode);
    }

    @GetMapping("${app.sub-endpoint.by-ingredients}")
    public Mono<Page<RecipeDto>> searchRecipesByIngredients(@RequestParam List<String> ingredients,
                                                                 @RequestParam(required = false) String mode,
                                                                 @RequestParam int page, @RequestParam int size) {
        return recipeService.findRecipesByIngredients(ingredients, page, size, mode);
    }

    @GetMapping("${app.sub-endpoint.by-all-ingredients}")
    public Mono<Page<RecipeDto>> searchRecipesByAllIngredients(@RequestParam List<String> ingredients,
                                                                 @RequestParam(required = false) String mode,
                                                                 @RequestParam int page, @RequestParam int size) {
        return recipeService.findRecipesByAllIngredients(ingredients, page, size, mode);
    }

    @GetMapping("${app.sub-endpoint.id.path-variable-id}")
    public Mono<RecipeDto> getRecipeById(@PathVariable String id) {
        return recipeService.getRecipeById(id);
    }

    @PostMapping("${app.sub-endpoint.by-ids}")
    public Mono<Map<String, Object>> getRecipesByIds(@RequestBody List<String> ids) {
        return recipeService.getRecipesByIds(ids);
    }

    @GetMapping("${app.sub-endpoint.name.path-variable-name}")
    public Mono<RecipeDto> getRecipeByName(@PathVariable String name) {
        return recipeService.getRecipeByName(name);
    }

    @PostMapping
    public Mono<RecipeDto> createRecipe(@RequestBody RecipeDto recipeDto) {
        return recipeService.createRecipe(recipeDto);
    }

    @PostMapping("${app.sub-endpoint.bulk}")
    public Flux<RecipeDto> createRecipes(@RequestBody Flux<RecipeDto> recipes) {
        return recipeService.createRecipes(recipes);
    }

    @DeleteMapping("${app.sub-endpoint.path-variable-id}")
    public Mono<String> deleteRecipeById(@PathVariable String id) {
        return recipeService.deleteRecipe(id);
    }

    @DeleteMapping("${app.sub-endpoint.all}")
    public Mono<Void> deleteAllRecipes() {
        return recipeService.deleteAllRecipes();
    }

}

