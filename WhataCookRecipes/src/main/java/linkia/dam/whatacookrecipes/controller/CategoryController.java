package linkia.dam.whatacookrecipes.controller;

import linkia.dam.whatacookrecipes.model.CategoryDto;
import linkia.dam.whatacookrecipes.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping("${app.endpoint.categories}")
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public Mono<Page<CategoryDto>> getAllCategories(@RequestParam(required = false) String mode,
                                                    @RequestParam int page, @RequestParam int size) {
        return categoryService.getAllCategories(page, size, mode);
    }

    @GetMapping("/searchPaged")
    public Mono<Page<CategoryDto>> getCategoriesByNameContaining(@RequestParam String name,
                                                                 @RequestParam(required = false) String mode,
                                                                 @RequestParam int page, @RequestParam int size) {
        return categoryService.getCategoriesByNameContaining(name, page, size, mode);
    }

    @GetMapping("id/{id}")
    public Mono<CategoryDto> getCategoryById(@PathVariable String id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping("name/{name}")
    public Mono<CategoryDto> getCategoryByName(@PathVariable String name) {
        return categoryService.getCategoryByNameIgnoreCase(name);
    }

    @PostMapping
    public Mono<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.createCategory(categoryDto);
    }

    @PostMapping("/bulk")
    public Flux<CategoryDto> createCategories(@RequestBody Flux<CategoryDto> categories) {
        return categoryService.createCategories(categories);
    }

    @DeleteMapping("/{id}")
    public Mono<String> deleteCategoryById(@PathVariable String id) {
        return categoryService.deleteCategory(id);
    }

    @DeleteMapping("/all")
    public Mono<Void> deleteAllCategories() {
        return categoryService.deleteAllCategories();
    }

}
