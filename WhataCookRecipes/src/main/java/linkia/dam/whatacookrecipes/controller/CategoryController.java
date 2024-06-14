package linkia.dam.whatacookrecipes.controller;

import linkia.dam.whatacookrecipes.model.CategoryDto;
import linkia.dam.whatacookrecipes.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing categories.
 * Provides endpoints for CRUD operations on categories.
 * <p>
 * Annotations:
 * - @AllArgsConstructor: Generates a constructor with 1 parameter for each field in the class.
 * - @RestController: Indicates that this class is a REST controller.
 * - @RequestMapping: Specifies the base URL for all endpoints in this controller.
 * - @Validated: Enables validation for the controller.
 * <p>
 * Methods:
 * - getAllCategories(String mode, int page, int size): Retrieves all categories with pagination.
 * - getCategoriesByNameContaining(String name, String mode, int page, int size): Retrieves categories by name containing a string with pagination.
 * - getCategoryById(String id): Retrieves a category by its ID.
 * - getCategoryByName(String name): Retrieves a category by its name, case insensitive.
 * - createCategory(CategoryDto categoryDto): Creates a new category.
 * - createCategories(Flux<CategoryDto> categories): Creates multiple categories.
 * - deleteCategoryById(String id): Deletes a category by its ID.
 * - deleteAllCategories(): Deletes all categories.
 *
 * @Author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see CategoryService
 */
@AllArgsConstructor
@RestController
@RequestMapping("${app.endpoint.categories}")
@Validated
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Retrieves all categories with pagination.
     *
     * @param mode The sorting mode.
     * @param page The page number to retrieve.
     * @param size The number of items per page.
     * @return A {@link Mono} emitting a {@link Page} of {@link CategoryDto} objects.
     */
    @GetMapping
    public Mono<Page<CategoryDto>> getAllCategories(@RequestParam(required = false) String mode,
                                                    @RequestParam int page, @RequestParam int size) {
        return categoryService.getAllCategories(page, size, mode);
    }

    /**
     * Retrieves categories by name containing a string with pagination.
     *
     * @param name The string to search for within category names.
     * @param mode The sorting mode.
     * @param page The page number to retrieve.
     * @param size The number of items per page.
     * @return A {@link Mono} emitting a {@link Page} of {@link CategoryDto} objects.
     */
    @GetMapping("${app.sub-endpoint.by-name}")
    public Mono<Page<CategoryDto>> getCategoriesByNameContaining(@RequestParam String name,
                                                                 @RequestParam(required = false) String mode,
                                                                 @RequestParam int page, @RequestParam int size) {
        return categoryService.getCategoriesByNameContaining(name, page, size, mode);
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param id The ID of the category to retrieve.
     * @return A {@link Mono} emitting the {@link CategoryDto} object, or an error if not found.
     */
    @GetMapping("${app.sub-endpoint.id.path-variable-id}")
    public Mono<CategoryDto> getCategoryById(@PathVariable String id) {
        return categoryService.getCategoryById(id);
    }

    /**
     * Retrieves a category by its name, case insensitive.
     *
     * @param name The name of the category to retrieve.
     * @return A {@link Mono} emitting the {@link CategoryDto} object, or an error if not found.
     */
    @GetMapping("${app.sub-endpoint.name.path-variable-name}")
    public Mono<CategoryDto> getCategoryByName(@PathVariable String name) {
        return categoryService.getCategoryByNameIgnoreCase(name);
    }

    /**
     * Creates a new category.
     *
     * @param categoryDto The category data to create.
     * @return A {@link Mono} emitting the created {@link CategoryDto} object.
     */
    @PostMapping
    public Mono<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto) {
        return categoryService.createCategory(categoryDto);
    }

    /**
     * Creates multiple categories.
     *
     * @param categories The flux of categories to create.
     * @return A {@link Flux} emitting the created {@link CategoryDto} objects.
     */
    @PostMapping("${app.sub-endpoint.bulk}")
    public Flux<CategoryDto> createCategories(@RequestBody Flux<CategoryDto> categories) {
        return categoryService.createCategories(categories);
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id The ID of the category to delete.
     * @return A {@link Mono} emitting a message indicating the category has been deleted, or an error if not found.
     */
    @DeleteMapping("${app.sub-endpoint.path-variable-id}")
    public Mono<String> deleteCategoryById(@PathVariable String id) {
        return categoryService.deleteCategory(id);
    }

    /**
     * Deletes all categories.
     *
     * @return A {@link Mono} indicating completion of the deletion process.
     */
    @DeleteMapping("${app.sub-endpoint.all}")
    public Mono<Void> deleteAllCategories() {
        return categoryService.deleteAllCategories();
    }

}
