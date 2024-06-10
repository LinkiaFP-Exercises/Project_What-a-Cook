package linkia.dam.whatacookrecipies.service;

import linkia.dam.whatacookrecipies.model.CategoryDto;
import linkia.dam.whatacookrecipies.service.contracts.CategoryDao;
import linkia.dam.whatacookrecipies.utilities.PaginationUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class CategoryService {

    private final CategoryDao categoryDao;

    public Mono<Page<CategoryDto>> getAllCategories(int page, int size, String mode) {
        return categoryDao.findAll().collectList()
                .flatMap(list -> PaginationUtil.createPagedResult(list, page, size, mode, CategoryDto.class));
    }

    public Mono<Page<CategoryDto>> getCategoriesByNameContaining(String name, int page, int size, String mode) {
        return categoryDao.findByNameContainingIgnoreCase(name).collectList()
                .flatMap(list -> PaginationUtil.createPagedResult(list, page, size, mode, CategoryDto.class));
    }

    public Mono<CategoryDto> getCategoryById(String id) {
        return categoryDao.findById(id);
    }
    public Mono<CategoryDto> getCategoryByName(String name) {
        return categoryDao.findByNameIgnoreCase(name);
    }

    public Mono<CategoryDto> createCategory(CategoryDto categoryDto) {
        return categoryDao.findByNameIgnoreCase(categoryDto.getName())
                .switchIfEmpty(Mono.defer(() -> categoryDao.save(categoryDto)));
    }

    public Flux<CategoryDto> createCategories(Flux<CategoryDto> categories) {
        return categories.flatMap(this::createCategory);
    }

    public Mono<Void> deleteCategory(String id) {
        return categoryDao.deleteById(id);
    }

    public Mono<ResponseEntity<String>> deleteCategory(CategoryDto categoryDto) {
        return categoryDao.findByNameIgnoreCase(categoryDto.getName())
                .flatMap(existingCategory -> categoryDao.delete(existingCategory)
                        .then(Mono.just(ResponseEntity.ok("Category " + existingCategory.getName() + " has been deleted."))))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    public Mono<Void> deleteAllCategories() {
        return categoryDao.deleteAll();
    }

}
