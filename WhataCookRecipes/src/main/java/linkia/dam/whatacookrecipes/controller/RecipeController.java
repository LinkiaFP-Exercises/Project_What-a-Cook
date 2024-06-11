package linkia.dam.whatacookrecipes.controller;

import linkia.dam.whatacookrecipes.model.RecipeDto;
import linkia.dam.whatacookrecipes.service.RecipeService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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

    @GetMapping("/searchPaged")
    public Mono<Page<RecipeDto>> getRecipesByNameContaining(@RequestParam String name,
                                                                 @RequestParam(required = false) String mode,
                                                                 @RequestParam int page, @RequestParam int size) {
        return recipeService.getRecipesByNameContaining(name, page, size, mode);
    }

    @GetMapping("id/{id}")
    public Mono<RecipeDto> getRecipeById(@PathVariable String id) {
        return recipeService.getRecipeById(id);
    }

    @GetMapping("name/{name}")
    public Mono<RecipeDto> getRecipeByName(@PathVariable String name) {
        return recipeService.getRecipeByName(name);
    }

    @PostMapping
    public Mono<RecipeDto> createRecipe(@RequestBody RecipeDto recipeDto) {
        return recipeService.createRecipe(recipeDto);
    }

    @PostMapping("/bulk")
    public Flux<RecipeDto> createRecipes(@RequestBody Flux<RecipeDto> recipes) {
        return recipeService.createRecipes(recipes);
    }

    @DeleteMapping("/{id}")
    public Mono<String> deleteRecipeById(@PathVariable String id) {
        return recipeService.deleteRecipe(id);
    }

    @DeleteMapping("/all")
    public Mono<Void> deleteAllRecipes() {
        return recipeService.deleteAllRecipes();
    }

}

