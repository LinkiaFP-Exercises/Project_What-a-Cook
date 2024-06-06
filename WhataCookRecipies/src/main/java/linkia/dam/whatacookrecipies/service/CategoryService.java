package linkia.dam.whatacookrecipies.service;

import linkia.dam.whatacookrecipies.model.CategoryDto;
import linkia.dam.whatacookrecipies.service.contracts.CategoryDao;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static linkia.dam.whatacookrecipies.utilities.PaginationUtil.createPagedResult;
import static linkia.dam.whatacookrecipies.utilities.ServiceUtil.sortByName;

@AllArgsConstructor
@Service
public class CategoryService {

    private final CategoryDao categoryDao;

    public Mono<Page<CategoryDto>> getAllCategories(int page, int size, String direction) {
        Pageable pageable = PageRequest.of(page, size, sortByName(direction));
        Mono<Long> count = categoryDao.count();
        Flux<CategoryDto> items = categoryDao.findAllBy(pageable);
        return createPagedResult(items, count, pageable);
    }

    public Mono<Page<CategoryDto>> getCategoriesByNameContaining(String name, int page, int size, String direction) {
        Pageable pageable = PageRequest.of(page, size, sortByName(direction));
        Mono<Long> count = categoryDao.count();
        Flux<CategoryDto> items = categoryDao.findByNameContainingIgnoreCase(name, pageable);
        return createPagedResult(items, count, pageable);
    }
/*
    public Mono<Page<CategoryDto>> getAllCategories(int page, int size, String direction) {
        Pageable pageable = PageRequest.of(page, size, sortByName(direction));
        return categoryDao.count()
                .flatMap(categoryCount -> categoryDao.findAllBy(pageable)
                        .buffer(pageable.getPageSize(), pageable.getPageNumber() + 1)
                        .elementAt(pageable.getPageNumber(), new ArrayList<>())
                        .map(categories -> new PageImpl<>(categories, pageable, categoryCount)));
    }

    public Mono<Page<CategoryDto>> getCategoriesByNameContaining(String name, int page, int size, String direction) {
        Pageable pageable = PageRequest.of(page, size, sortByName(direction));
        return categoryDao.count()
                .flatMap(categoryCount -> categoryDao.findByNameContainingIgnoreCase(name, pageable)
                        .buffer(pageable.getPageSize(), pageable.getPageNumber() + 1)
                        .elementAt(pageable.getPageNumber(), new ArrayList<>())
                        .map(categories -> new PageImpl<>(categories, pageable, categoryCount)));
    }
 */

    public Mono<CategoryDto> getCategoryById(String id) {
        return categoryDao.findById(id);
    }

    public Mono<CategoryDto> createCategory(CategoryDto categoryDto) {
        return categoryDao.save(categoryDto);
    }

    public Mono<Void> deleteCategory(String id) {
        return categoryDao.deleteById(id);
    }

    public Flux<CategoryDto> createCategories(Flux<CategoryDto> categories) {
        return categoryDao.saveAll(categories);
    }

}
