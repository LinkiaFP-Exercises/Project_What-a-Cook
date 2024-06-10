package linkia.dam.whatacookrecipies.controller.categories;

import linkia.dam.whatacookrecipies.model.CategoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.mockito.Mockito.when;

public class TestGetAllCategories extends BaseCategoriesTest {

    protected int page;
    protected int size;

    protected CategoryDto getExpectedCategoryDto(int page, boolean desc) {
        List<CategoryDto> sortedList = new ArrayList<>(categoryDtoList);
        if (desc) {
            sortedList.sort((a, b) -> b.getName().compareTo(a.getName()));
        } else {
            sortedList.sort(Comparator.comparing(CategoryDto::getName));
        }
        int startIndex = page * size;
        return sortedList.get(startIndex);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        size = 10;
        amount = 36;
        categoryDtoList = generateCategoryDtoList(amount);

        when(categoryDao.findAll()).thenReturn(Flux.fromIterable(categoryDtoList));
    }

    @Test
    void getAllCategoriesPage0Asc() {
        page = 0;
        CategoryDto expectedFirstCategory = getExpectedCategoryDto(page, false);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(categoriesUri)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("mode", "")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(size)
                .jsonPath("$.content[0].id").isEqualTo(expectedFirstCategory.getId())
                .jsonPath("$.content[0].name").isEqualTo(expectedFirstCategory.getName())
                .jsonPath("$.pageable.pageNumber").isEqualTo(page)
                .jsonPath("$.pageable.pageSize").isEqualTo(size)
                .jsonPath("$.totalElements").isEqualTo(amount)
                .jsonPath("$.totalPages").isEqualTo((int) Math.ceil((double) amount / size))
                .jsonPath("$.first").isEqualTo(true)
                .jsonPath("$.last").isEqualTo(false);
    }

    @Test
    void getAllCategoriesPage0Desc() {
        page = 0;
        CategoryDto expectedFirstCategory = getExpectedCategoryDto(page, true);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(categoriesUri)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("mode", "D")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(size)
                .jsonPath("$.content[0].id").isEqualTo(expectedFirstCategory.getId())
                .jsonPath("$.content[0].name").isEqualTo(expectedFirstCategory.getName())
                .jsonPath("$.pageable.pageNumber").isEqualTo(page)
                .jsonPath("$.pageable.pageSize").isEqualTo(size)
                .jsonPath("$.totalElements").isEqualTo(amount)
                .jsonPath("$.totalPages").isEqualTo((int) Math.ceil((double) amount / size))
                .jsonPath("$.first").isEqualTo(true)
                .jsonPath("$.last").isEqualTo(false);
    }

    @Test
    void getAllCategoriesPage3Asc() {
        page = 3;
        CategoryDto expectedFirstCategory = getExpectedCategoryDto(page, false);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(categoriesUri)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("mode", "")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(6)
                .jsonPath("$.content[0].id").isEqualTo(expectedFirstCategory.getId())
                .jsonPath("$.content[0].name").isEqualTo(expectedFirstCategory.getName())
                .jsonPath("$.pageable.pageNumber").isEqualTo(page)
                .jsonPath("$.pageable.pageSize").isEqualTo(size)
                .jsonPath("$.totalElements").isEqualTo(amount)
                .jsonPath("$.totalPages").isEqualTo((int) Math.ceil((double) amount / size))
                .jsonPath("$.first").isEqualTo(false)
                .jsonPath("$.last").isEqualTo(page == (amount / size))
                .jsonPath("$.numberOfElements").isEqualTo(6);
    }

    @Test
    void getAllCategoriesPage3Desc() {
        page = 3;
        CategoryDto expectedFirstCategory = getExpectedCategoryDto(page, true);
        System.out.println();
        System.out.println(categoryDtoList);
        System.out.println();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(categoriesUri)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("mode", "D")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(6)
                .jsonPath("$.content[0].id").isEqualTo(expectedFirstCategory.getId())
                .jsonPath("$.content[0].name").isEqualTo(expectedFirstCategory.getName())
                .jsonPath("$.pageable.pageNumber").isEqualTo(page)
                .jsonPath("$.pageable.pageSize").isEqualTo(size)
                .jsonPath("$.totalElements").isEqualTo(amount)
                .jsonPath("$.totalPages").isEqualTo((int) Math.ceil((double) amount / size))
                .jsonPath("$.first").isEqualTo(false)
                .jsonPath("$.last").isEqualTo(page == (amount / size))
                .jsonPath("$.numberOfElements").isEqualTo(6);
    }

}
