package linkia.dam.whatacookrecipies.controller.categories;

import linkia.dam.whatacookrecipies.model.CategoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

public class TestGetCategoriesByNameContaining extends BaseCategoriesTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        name = "D";
        size = 10;
        amount = 36;
        categoryDtoList = generateCategoryDtoList(amount);

        categoryDtoList = categoryDtoList.stream()
                .filter(category -> category.getName().contains(name))
                .collect(Collectors.toList());

        when(categoryDao.findByNameContainingIgnoreCase(name)).thenReturn(Flux.fromIterable(categoryDtoList));
    }

    private void validateResponse(String mode, String name, CategoryDto expectedFirstCategory, int numberOfElements) {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(categoriesUri + "/searchPaged")
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("mode", mode)
                        .queryParam("name", name)
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
                .jsonPath("$.totalElements").isEqualTo(categoryDtoList.size())
                .jsonPath("$.totalPages").isEqualTo((int) Math.ceil((double) categoryDtoList.size() / size))
                .jsonPath("$.first").isEqualTo(true)
                .jsonPath("$.last").isEqualTo(true);
    }

    @Test
    void getCategoriesByNameContainingAsc() {
        page = 0;
        CategoryDto expectedFirstCategory = getExpectedCategoryDto(false);

        validateResponse("", name, expectedFirstCategory, getNumberLastElements());
    }

    @Test
    void getCategoriesByNameContainingDesc() {
        page = 0;
        CategoryDto expectedFirstCategory = getExpectedCategoryDto(true);

        validateResponse("D", name, expectedFirstCategory, getNumberLastElements());
    }
}
