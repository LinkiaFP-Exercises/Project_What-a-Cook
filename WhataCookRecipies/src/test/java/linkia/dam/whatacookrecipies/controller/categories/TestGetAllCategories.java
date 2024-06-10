package linkia.dam.whatacookrecipies.controller.categories;

import linkia.dam.whatacookrecipies.model.CategoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.when;

public class TestGetAllCategories extends BaseCategoriesTest {



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        size = 10;
        when(categoryDao.findAll()).thenReturn(Flux.fromIterable(categoryDtoList));
    }

    private void validateResponse(String mode, CategoryDto expectedFirstCategory, boolean isFirst, boolean isLast, int numberOfElements) {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(categoriesUri)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("mode", mode)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(numberOfElements)
                .jsonPath("$.content[0].id").isEqualTo(expectedFirstCategory.getId())
                .jsonPath("$.content[0].name").isEqualTo(expectedFirstCategory.getName())
                .jsonPath("$.pageable.pageNumber").isEqualTo(page)
                .jsonPath("$.pageable.pageSize").isEqualTo(size)
                .jsonPath("$.totalElements").isEqualTo(amount)
                .jsonPath("$.totalPages").isEqualTo((int) Math.ceil((double) amount / size))
                .jsonPath("$.first").isEqualTo(isFirst)
                .jsonPath("$.last").isEqualTo(isLast);
    }

    @Test
    void getAllCategoriesPage0Asc() {
        page = 0;
        CategoryDto expectedFirstCategory = getExpectedCategoryDto(false, null);

        validateResponse("", expectedFirstCategory, true, false, size);
    }

    @Test
    void getAllCategoriesPage0Desc() {
        page = 0;
        CategoryDto expectedFirstCategory = getExpectedCategoryDto(true, null);

        validateResponse("D", expectedFirstCategory, true, false, size);
    }

    @Test
    void getAllCategoriesPage3Asc() {
        page = 3;
        CategoryDto expectedFirstCategory = getExpectedCategoryDto(false, null);

        validateResponse("", expectedFirstCategory, false, true, getNumberLastElements());
    }

    @Test
    void getAllCategoriesPage3Desc() {
        page = 3;
        CategoryDto expectedFirstCategory = getExpectedCategoryDto(true, null);

        validateResponse("D", expectedFirstCategory, false, true, getNumberLastElements());
    }
}
