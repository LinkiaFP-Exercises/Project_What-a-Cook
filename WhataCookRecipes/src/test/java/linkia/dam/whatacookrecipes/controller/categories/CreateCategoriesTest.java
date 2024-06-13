package linkia.dam.whatacookrecipes.controller.categories;

import linkia.dam.whatacookrecipes.model.CategoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CreateCategoriesTest extends BaseCategoriesTest {

    private Flux<CategoryDto> categoryDtoFlux;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryDtoFlux = Flux.fromIterable(categoryDtoList);
    }

    @Test
    void createCategoriesNew() {
        when(categoryDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(categoryDao.save(any(CategoryDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        testCreateCategories();
    }

    @Test
    void createCategoriesExistingCategories() {
        when(categoryDao.findByNameIgnoreCase(anyString())).thenAnswer(invocation -> {
            String name = invocation.getArgument(0);
            return categoryDtoList.stream()
                    .filter(categoryDto -> categoryDto.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .map(Mono::just)
                    .orElse(Mono.empty());
        });

        testCreateCategories();

        verify(categoryDao, times(0)).save(any(CategoryDto.class));
    }

    private void testCreateCategories() {
        webTestClient.post()
                .uri(categoriesUri + PATH_Bulk)
                .body(categoryDtoFlux, CategoryDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CategoryDto.class)
                .hasSize(amount)
                .value(categories -> {
                    List<String> responseNames = categories.stream().map(CategoryDto::getName).toList();
                    assert responseNames.contains(categoryDtoList.getFirst().getName());
                    assert responseNames.contains(categoryDtoList.get(amount - 1).getName());
                });
    }

}
