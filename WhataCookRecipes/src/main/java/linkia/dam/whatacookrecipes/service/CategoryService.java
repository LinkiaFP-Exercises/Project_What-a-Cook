package linkia.dam.whatacookrecipes.service;

import linkia.dam.whatacookrecipes.model.CategoryDto;
import linkia.dam.whatacookrecipes.model.exception.ResourceNotFoundException;
import linkia.dam.whatacookrecipes.service.repository.CategoryDao;
import linkia.dam.whatacookrecipes.utilities.PaginationUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service class for managing categories.
 * Provides methods for CRUD operations and additional business logic.
 * <p>
 * Annotations:
 * - @AllArgsConstructor: Generates a constructor with 1 parameter for each field in the class.
 * - @Service: Indicates that this class is a Spring service component.
 * <p>
 * Methods:
 * - getAllCategories(int page, int size, String mode): Retrieves all categories with pagination.
 * - getCategoriesByNameContaining(String name, int page, int size, String mode): Retrieves categories by name containing a string with pagination.
 * - getCategoryById(String id): Retrieves a category by its ID.
 * - getCategoryByNameIgnoreCase(String name): Retrieves a category by its name, case insensitive.
 * - createCategory(CategoryDto categoryDto): Creates a new category if it does not already exist.
 * - createCategories(Flux<CategoryDto> categories): Creates multiple categories.
 * - deleteCategory(String id): Deletes a category by its ID.
 * - deleteAllCategories(): Deletes all categories.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see CategoryDao
 * @see PaginationUtil
 */
@AllArgsConstructor
@Service
public class CategoryService {

    private final CategoryDao categoryDao;

    /**
     * Retrieves all categories with pagination.
     *
     * @param page The page number to retrieve.
     * @param size The number of items per page.
     * @param mode The pagination mode.
     * @return A {@link Mono} emitting a {@link Page} of {@link CategoryDto} objects.
     */
    public Mono<Page<CategoryDto>> getAllCategories(int page, int size, String mode) {
        return categoryDao.findAll().collectList()
                .flatMap(list -> PaginationUtil.createPagedResult(list, page, size, mode));
    }

    /**
     * Retrieves categories by name containing a string with pagination.
     *
     * @param name The string to search for within category names.
     * @param page The page number to retrieve.
     * @param size The number of items per page.
     * @param mode The pagination mode.
     * @return A {@link Mono} emitting a {@link Page} of {@link CategoryDto} objects.
     */
    public Mono<Page<CategoryDto>> getCategoriesByNameContaining(String name, int page, int size, String mode) {
        return categoryDao.findByNameContainingIgnoreCase(name).collectList()
                .flatMap(list -> PaginationUtil.createPagedResult(list, page, size, mode));
    }

    /**
     * Retrieves a category by its ID.
     *
     * @param id The ID of the category to retrieve.
     * @return A {@link Mono} emitting the {@link CategoryDto} object, or an error if not found.
     */
    public Mono<CategoryDto> getCategoryById(String id) {
        return categoryDao.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Category not found with id=" + id)));
    }

    /**
     * Retrieves a category by its name, case insensitive.
     *
     * @param name The name of the category to retrieve.
     * @return A {@link Mono} emitting the {@link CategoryDto} object, or an error if not found.
     */
    public Mono<CategoryDto> getCategoryByNameIgnoreCase(String name) {
        return categoryDao.findByNameIgnoreCase(name)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Category not found with name=" + name)));
    }

    /**
     * Creates a new category if it does not already exist.
     *
     * @param categoryDto The category data to create.
     * @return A {@link Mono} emitting the created {@link CategoryDto} object.
     */
    public Mono<CategoryDto> createCategory(CategoryDto categoryDto) {
        return categoryDao.findByNameIgnoreCase(categoryDto.getName())
                .switchIfEmpty(Mono.defer(() -> categoryDao.save(categoryDto)));
    }

    /**
     * Creates multiple categories.
     *
     * @param categories The categories to create.
     * @return A {@link Flux} emitting the created {@link CategoryDto} objects.
     */
    public Flux<CategoryDto> createCategories(Flux<CategoryDto> categories) {
        return categories.flatMap(this::createCategory);
    }

    /**
     * Deletes a category by its ID.
     *
     * @param id The ID of the category to delete.
     * @return A {@link Mono} emitting a message indicating the category has been deleted, or an error if not found.
     */
    public Mono<String> deleteCategory(String id) {
        return categoryDao.findById(id)
                .flatMap(existingCategory -> categoryDao.delete(existingCategory)
                        .then(Mono.just("Category " + existingCategory.getName() + " has been deleted.")))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Category not found with id=" + id)));
    }

    /**
     * Deletes all categories.
     *
     * @return A {@link Mono} indicating completion of the deletion process.
     */
    public Mono<Void> deleteAllCategories() {
        return categoryDao.deleteAll();
    }

}
