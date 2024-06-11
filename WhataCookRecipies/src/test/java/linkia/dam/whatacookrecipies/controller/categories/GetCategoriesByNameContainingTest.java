package linkia.dam.whatacookrecipies.controller.categories;

import linkia.dam.whatacookrecipies.model.CategoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

public class GetCategoriesByNameContainingTest extends BaseCategoriesTest {

    public List<CategoryDto> categoryDtoListFiltered;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        name = "D";
        size = 10;

        categoryDtoListFiltered = categoryDtoList.stream()
                .filter(category -> category.getName().contains(name))
                .collect(Collectors.toList());

        when(categoryDao.findByNameContainingIgnoreCase(name)).thenReturn(Flux.fromIterable(categoryDtoListFiltered));
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
                .jsonPath("$.totalElements").isEqualTo(categoryDtoListFiltered.size())
                .jsonPath("$.totalPages").isEqualTo((int) Math.ceil((double) categoryDtoListFiltered.size() / size))
                .jsonPath("$.first").isEqualTo(true)
                .jsonPath("$.last").isEqualTo(true);
    }

    @Test
    void getCategoriesByNameContainingAsc() {
        page = 0;
        CategoryDto expectedFirstCategory = getExpectedCategoryDto(false, categoryDtoListFiltered);

        validateResponse("", name, expectedFirstCategory, categoryDtoListFiltered.size());
    }

    @Test
    void getCategoriesByNameContainingDesc() {
        page = 0;
        CategoryDto expectedFirstCategory = getExpectedCategoryDto(true, categoryDtoListFiltered);

        validateResponse("D", name, expectedFirstCategory, categoryDtoListFiltered.size());
    }
}
