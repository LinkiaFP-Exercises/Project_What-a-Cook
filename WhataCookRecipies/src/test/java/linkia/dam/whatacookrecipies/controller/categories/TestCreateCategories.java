package linkia.dam.whatacookrecipies.controller.categories;

import linkia.dam.whatacookrecipies.model.CategoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TestCreateCategories extends BaseCategoriesTest {




    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        amount = 36;
        categoryDtoList = generateCategoryDtoList(amount);
    }

    @Test
    void createCategories() {
        Flux<CategoryDto> categoryDtoFlux = Flux.fromIterable(categoryDtoList);

        when(categoryService.createCategories(any(Flux.class))).thenReturn(categoryDtoFlux);

        webTestClient.post()
                .uri(categoriesUri + "/bulk")
                .body(categoryDtoFlux, CategoryDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CategoryDto.class)
                .hasSize(amount)
                .contains(categoryDtoList.toArray(new CategoryDto[0]));
    }
}
