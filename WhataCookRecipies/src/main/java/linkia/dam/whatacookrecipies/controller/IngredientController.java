package linkia.dam.whatacookrecipies.controller;

import linkia.dam.whatacookrecipies.model.IngredientDto;
import linkia.dam.whatacookrecipies.service.IngredientService;
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

    @GetMapping("/searchPaged")
    public Mono<Page<IngredientDto>> getCategoriesByNameContaining(@RequestParam String name,
                                                                 @RequestParam(required = false) String mode,
                                                                 @RequestParam int page, @RequestParam int size) {
        return ingredientService.getCategoriesByNameContaining(name, page, size, mode);
    }

    @GetMapping("id/{id}")
    public Mono<IngredientDto> getIngredientById(@PathVariable String id) {
        return ingredientService.getIngredientById(id);
    }

    @GetMapping("name/{name}")
    public Mono<IngredientDto> getIngredientByName(@PathVariable String name) {
        return ingredientService.getIngredientByName(name);
    }

    @PostMapping
    public Mono<IngredientDto> createIngredient(@RequestBody IngredientDto ingredientDto) {
        return ingredientService.createIngredient(ingredientDto);
    }

    @PostMapping("/bulk")
    public Flux<IngredientDto> createCategories(@RequestBody Flux<IngredientDto> ingredients) {
        return ingredientService.createCategories(ingredients);
    }

    @DeleteMapping("/{id}")
    public Mono<String> deleteIngredientById(@PathVariable String id) {
        return ingredientService.deleteIngredient(id);
    }

    @DeleteMapping("/all")
    public Mono<Void> deleteAllCategories() {
        return ingredientService.deleteAllCategories();
    }

}
