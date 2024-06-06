package linkia.dam.whatacookrecipies.controller;

import linkia.dam.whatacookrecipies.model.CategoryDto;
import linkia.dam.whatacookrecipies.service.CategoryService;
import linkia.dam.whatacookrecipies.utilities.PagedResponse;
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
    public Mono<PagedResponse<CategoryDto>> getAllCategories(@RequestParam int page,
                                                             @RequestParam int size) {
        return categoryService.getAllCategories(page, size);
    }

    @GetMapping("/searchPaged")
    public Mono<PagedResponse<CategoryDto>> getCategoriesByNameContaining(@RequestParam String name,
                                                                          @RequestParam int page,
                                                                          @RequestParam int size) {
        return categoryService.getCategoriesByNameContaining(name, page, size);
    }

/*
    @GetMapping
    public Flux<CategoryDto> getAllCategories(@RequestParam int page,
                                                    @RequestParam int size,
                                                    @RequestParam String direction) {
        return categoryService.getAllCategories(page, size, direction);
    }

    @GetMapping("/searchPaged")
    public Flux<CategoryDto> getCategoriesByNameContaining(@RequestParam String name,
                                                                 @RequestParam int page,
                                                                 @RequestParam int size,
                                                                 @RequestParam String direction) {
        return categoryService.getCategoriesByNameContaining(name, page, size, direction);
    }

 */

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
