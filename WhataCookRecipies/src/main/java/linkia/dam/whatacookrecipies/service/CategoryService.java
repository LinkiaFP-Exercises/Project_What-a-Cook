package linkia.dam.whatacookrecipies.service;

import linkia.dam.whatacookrecipies.model.CategoryDto;
import linkia.dam.whatacookrecipies.service.contracts.CategoryDao;
import linkia.dam.whatacookrecipies.utilities.PaginationUtil;
import linkia.dam.whatacookrecipies.utilities.ServiceUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class CategoryService {

    private final CategoryDao categoryDao;

    public Mono<Page<CategoryDto>> getAllCategories(int page, int size, String direction) {
        Flux<CategoryDto> items = categoryDao.findAll();
        return getPagedCategories(items, page, size, direction);
    }

    public Mono<Page<CategoryDto>> getCategoriesByNameContaining(String name, int page, int size, String direction) {
        Flux<CategoryDto> items = categoryDao.findByNameContainingIgnoreCase(name);
        return getPagedCategories(items, page, size, direction);
    }

    private Mono<Page<CategoryDto>> getPagedCategories(Flux<CategoryDto> items, int page, int size, String direction) {
        Pageable pageable = PageRequest.of(page, size, ServiceUtil.sortByName(direction));
        Mono<Long> count = categoryDao.count();
        return PaginationUtil.createPagedResult(items, count, pageable, CategoryDto.class);
    }


    public Mono<CategoryDto> getCategoryById(String id) {
        return categoryDao.findById(id);
    }

    public Mono<CategoryDto> createCategory(CategoryDto categoryDto) {
        return categoryDao.save(categoryDto);
    }

    public Mono<Void> deleteCategory(String id) {
        return categoryDao.deleteById(id);
    }

    public Mono<Void> deleteCategorysAll() {
        return categoryDao.deleteAll();
    }

    public Flux<CategoryDto> createCategories(Flux<CategoryDto> categories) {
        return categoryDao.saveAll(categories);
    }

}
