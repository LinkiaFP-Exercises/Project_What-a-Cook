package linkia.dam.whatacookrecipies.service;

import linkia.dam.whatacookrecipies.model.CategoryDto;
import linkia.dam.whatacookrecipies.model.exception.ResourceNotFoundException;
import linkia.dam.whatacookrecipies.service.contracts.CategoryDao;
import linkia.dam.whatacookrecipies.utilities.PaginationUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class CategoryService {

    private final CategoryDao categoryDao;

    public Mono<Page<CategoryDto>> getAllCategories(int page, int size, String mode) {
        return categoryDao.findAll().collectList()
                .flatMap(list -> PaginationUtil.createPagedResult(list, page, size, mode));
    }

    public Mono<Page<CategoryDto>> getCategoriesByNameContaining(String name, int page, int size, String mode) {
        return categoryDao.findByNameContainingIgnoreCase(name).collectList()
                .flatMap(list -> PaginationUtil.createPagedResult(list, page, size, mode));
    }

    public Mono<CategoryDto> getCategoryById(String id) {
        return categoryDao.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Category not found with id=" + id)));
    }
    public Mono<CategoryDto> getCategoryByName(String name) {
        return categoryDao.findByNameIgnoreCase(name)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Category not found with name=" + name)));
    }

    public Mono<CategoryDto> createCategory(CategoryDto categoryDto) {
        return categoryDao.findByNameIgnoreCase(categoryDto.getName())
                .switchIfEmpty(Mono.defer(() -> categoryDao.save(categoryDto)));
    }

    public Flux<CategoryDto> createCategories(Flux<CategoryDto> categories) {
        return categories.flatMap(this::createCategory);
    }

    public Mono<String> deleteCategory(String id) {
        return categoryDao.findById(id)
                .flatMap(existingCategory -> categoryDao.delete(existingCategory)
                        .then(Mono.just("Category " + existingCategory.getName() + " has been deleted.")))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Category not found with id=" + id)));
    }

    public Mono<Void> deleteAllCategories() {
        return categoryDao.deleteAll();
    }

}
