package linkia.dam.whatacookrecipes.controller;

import linkia.dam.whatacookrecipes.model.IngredientDto;
import linkia.dam.whatacookrecipes.service.IngredientService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping("${app.endpoint.ingredients}")
@Validated
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping
    public Mono<Page<IngredientDto>> getAllCategories(@RequestParam(required = false) String mode,
                                                    @RequestParam int page, @RequestParam int size) {
        return ingredientService.getAllCategories(page, size, mode);
    }

    @GetMapping("${app.sub-endpoint.by-name}")
    public Mono<Page<IngredientDto>> getCategoriesByNameContaining(@RequestParam String name,
                                                                 @RequestParam(required = false) String mode,
                                                                 @RequestParam int page, @RequestParam int size) {
        return ingredientService.getCategoriesByNameContaining(name, page, size, mode);
    }

    @GetMapping("${app.sub-endpoint.id.path-variable-id}")
    public Mono<IngredientDto> getIngredientById(@PathVariable String id) {
        return ingredientService.getIngredientById(id);
    }

    @GetMapping("${app.sub-endpoint.name.path-variable-name}")
    public Mono<IngredientDto> getIngredientByName(@PathVariable String name) {
        return ingredientService.getIngredientByName(name);
    }

    @PostMapping
    public Mono<IngredientDto> createIngredient(@RequestBody IngredientDto ingredientDto) {
        return ingredientService.createIngredient(ingredientDto);
    }

    @PostMapping("${app.sub-endpoint.bulk}")
    public Flux<IngredientDto> createCategories(@RequestBody Flux<IngredientDto> ingredients) {
        return ingredientService.createCategories(ingredients);
    }

    @DeleteMapping("${app.sub-endpoint.path-variable-id}")
    public Mono<String> deleteIngredientById(@PathVariable String id) {
        return ingredientService.deleteIngredient(id);
    }

    @DeleteMapping("${app.sub-endpoint.all}")
    public Mono<Void> deleteAllCategories() {
        return ingredientService.deleteAllCategories();
    }

}
