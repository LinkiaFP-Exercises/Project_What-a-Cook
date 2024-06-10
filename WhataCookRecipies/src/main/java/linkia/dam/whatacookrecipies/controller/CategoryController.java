package linkia.dam.whatacookrecipies.controller;

import linkia.dam.whatacookrecipies.model.CategoryDto;
import linkia.dam.whatacookrecipies.model.exception.ResourceNotFoundException;
import linkia.dam.whatacookrecipies.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("/{id}")
    public Mono<CategoryDto> getCategoryById(@PathVariable String id) {
        return categoryService.getCategoryById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Category not found with id=" + id)));
    }

    @GetMapping("/{name}")
    public Mono<CategoryDto> getCategoryByName(@PathVariable String name) {
        return categoryService.getCategoryByName(name)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Category not found with name=" + name)));
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
    public Mono<Void> deleteCategory(@PathVariable String id) {
        return categoryService.deleteCategory(id);
    }

    @DeleteMapping
    public Mono<ResponseEntity<String>> deleteCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.deleteCategory(categoryDto);
    }

    @DeleteMapping("/all")
    public Mono<Void> deleteAllCategories() {
        return categoryService.deleteAllCategories();
    }

}
