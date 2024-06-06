package linkia.dam.whatacookrecipies.controller;

import linkia.dam.whatacookrecipies.model.CategoryDto;
import linkia.dam.whatacookrecipies.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping("/api/categories")
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public Mono<Page<CategoryDto>>
        getAllCategories(@RequestParam(required = false, defaultValue = "asc") String direction,
                                @RequestParam int page, @RequestParam int size) {
        return categoryService.getAllCategories(page, size, direction);
    }

    @GetMapping("/searchPaged")
    public Mono<Page<CategoryDto>>
        getCategoriesByNameContaining(@RequestParam(required = false, defaultValue = "asc") String direction,
                                         @RequestParam String name, @RequestParam int page, @RequestParam int size) {
        return categoryService.getCategoriesByNameContaining(name, page, size, direction);
    }

    @GetMapping("/{id}")
    public Mono<CategoryDto> getCategoryById(@PathVariable String id) {
        return categoryService.getCategoryById(id);
    }

    @PostMapping
    public Mono<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.createCategory(categoryDto);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteCategory(@PathVariable String id) {
        return categoryService.deleteCategory(id);
    }

    @PostMapping("/bulk")
    public Flux<CategoryDto> createCategories(@RequestBody Flux<CategoryDto> categories) {
        return categoryService.createCategories(categories);
    }

}
