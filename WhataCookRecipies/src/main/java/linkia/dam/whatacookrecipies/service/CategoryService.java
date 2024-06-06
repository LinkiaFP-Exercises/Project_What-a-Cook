package linkia.dam.whatacookrecipies.service;

import linkia.dam.whatacookrecipies.model.CategoryDto;
import linkia.dam.whatacookrecipies.service.contracts.CategoryDao;
import linkia.dam.whatacookrecipies.utilities.PagedResponse;
import linkia.dam.whatacookrecipies.utilities.ServiceUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static linkia.dam.whatacookrecipies.utilities.ServiceUtil.sortByName;

@AllArgsConstructor
@Service
public class CategoryService {

    private final CategoryDao categoryDao;
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    public Flux<CategoryDto> getAllCategories() {
        return categoryDao.findAll();
    }

    public Mono<PagedResponse<CategoryDto>> getAllCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, ServiceUtil.sortByName("asc"));
        return ServiceUtil.aggregateCategories(reactiveMongoTemplate, pageable, null);
    }

    public Mono<PagedResponse<CategoryDto>> getCategoriesByNameContaining(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, ServiceUtil.sortByName("asc"));
        Criteria criteria = Criteria.where("name").regex(name, "i");
        return ServiceUtil.aggregateCategories(reactiveMongoTemplate, pageable, criteria);
    }
/*
    public Flux<CategoryDto> getAllCategories(int page, int size, String direction) {
        Pageable pageable = PageRequest.of(page, size, sortByName(direction));
        return categoryDao.findAllBy(pageable);
    }

    public Flux<CategoryDto> getCategoriesByNameContaining(String name, int page, int size, String direction) {
        Pageable pageable = PageRequest.of(page, size, sortByName(direction));
        return categoryDao.findByNameContainingIgnoreCase(name, pageable);
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
