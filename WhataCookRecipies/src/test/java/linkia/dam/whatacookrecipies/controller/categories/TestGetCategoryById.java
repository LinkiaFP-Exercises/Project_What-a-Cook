package linkia.dam.whatacookrecipies.controller.categories;

import linkia.dam.whatacookrecipies.model.CategoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class TestGetCategoryById extends BaseCategoriesTest {

    private CategoryDto categoryDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryDto = generateCategoryDto();
    }

    @Test
    void getCategoryByIdFound() {
        when(categoryDao.findById(anyString())).thenReturn(Mono.just(categoryDto));

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(categoriesUri + "/{id}")
                        .build(categoryDto.getId()))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(categoryDto.getId())
                .jsonPath("$.name").isEqualTo(categoryDto.getName());
    }

    @Test
    void getCategoryByIdNotFound() {
        when(categoryDao.findById(anyString())).thenReturn(Mono.empty());

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(categoriesUri + "/{id}")
                        .build("non-existent-id"))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
