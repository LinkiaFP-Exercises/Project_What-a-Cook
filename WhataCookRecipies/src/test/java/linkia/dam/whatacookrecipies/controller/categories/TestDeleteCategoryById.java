package linkia.dam.whatacookrecipies.controller.categories;

import linkia.dam.whatacookrecipies.model.CategoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class TestDeleteCategoryById extends BaseCategoriesTest {

    private final String deleted = "deleted";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryDto = generateCategoryDto();
        pathVariable = "/{id}";
        valuePathVariable = categoryDto.getId();
    }

    @Test
    void testDeleteCategoryByIdFounded() {
        when(categoryDao.findById(anyString())).thenReturn(Mono.just(categoryDto));
        when(categoryDao.delete(categoryDto)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder.path(categoriesUri + pathVariable)
                        .build(valuePathVariable))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> {
                    assert response.contains(categoryDto.getName());
                    assert response.contains(deleted);
                });
    }

    @Test
    void testDeleteCategoryByIdNotFound() {
        when(categoryDao.findById(anyString())).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder.path(categoriesUri + pathVariable)
                        .build(valuePathVariable))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

        verify(categoryDao, times(0)).delete(any(CategoryDto.class));
    }
}
