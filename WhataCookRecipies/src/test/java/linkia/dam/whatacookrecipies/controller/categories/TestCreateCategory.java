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
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

public class TestCreateCategory extends BaseCategoriesTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryDto = generateCategoryDto();
    }

    @Test
    void createCategory() {
        when(categoryDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(categoryDao.save(any(CategoryDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        webTestClient.post()
                .uri(categoriesUri)
                .contentType(APPLICATION_JSON)
                .body(fromValue(categoryDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(categoryDto.getId())
                .jsonPath("$.name").isEqualTo(categoryDto.getName());

        verify(categoryDao, times(1)).save(any(CategoryDto.class));
    }

    @Test
    void createCategoryAlreadyExists() {
        when(categoryDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.just(categoryDto));

        webTestClient.post()
                .uri(categoriesUri)
                .contentType(APPLICATION_JSON)
                .body(fromValue(categoryDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(categoryDto.getId())
                .jsonPath("$.name").isEqualTo(categoryDto.getName());

        verify(categoryDao, times(0)).save(any(CategoryDto.class));
    }
}
