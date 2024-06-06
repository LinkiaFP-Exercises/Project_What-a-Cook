package linkia.dam.whatacookrecipies.controller;

import linkia.dam.whatacookrecipies.model.IngredientDto;
import linkia.dam.whatacookrecipies.service.IngredientService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping("/api/ingredients")
@Validated
public class IngredientController {

    private final IngredientService ingredientService;

    @GetMapping
    public Flux<IngredientDto> getAllIngredients(@RequestParam int page, @RequestParam int size, @RequestParam String direction) {
        return ingredientService.getAllIngredients(page, size, direction);
    }

    @GetMapping("/{id}")
    public Mono<IngredientDto> getIngredientById(@PathVariable String id) {
        return ingredientService.getIngredientById(id);
    }

    @PostMapping
    public Mono<IngredientDto> createIngredient(@RequestBody IngredientDto ingredientDto) {
        return ingredientService.createIngredient(ingredientDto);
    }

    @PutMapping("/{id}")
    public Mono<IngredientDto> updateIngredient(@PathVariable String id, @RequestBody IngredientDto ingredientDto) {
        return ingredientService.updateIngredient(id, ingredientDto);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteIngredient(@PathVariable String id) {
        return ingredientService.deleteIngredient(id);
    }

}
