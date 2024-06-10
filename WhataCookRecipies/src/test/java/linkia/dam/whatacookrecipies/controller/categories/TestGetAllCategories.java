package linkia.dam.whatacookrecipies.controller.categories;

import com.fasterxml.jackson.databind.ObjectMapper;
import linkia.dam.whatacookrecipies.model.CategoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TestGetAllCategories extends BaseCategoriesTest {

    private int page;
    private int size;
    private int start;
    private int end;
    private List<CategoryDto> sublist;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        size = 10;
        amount = 36;
        categoryDtoList = generateCategoryDtoList(amount);
    }

    @Test
    void getAllCategoriesPage0Asc() throws Exception {
        page = 0;
        defineSublist();

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
                .jsonPath("$.content[0].id").isEqualTo("id-1")
                .jsonPath("$.content[0].name").isEqualTo("Category 1")
                .jsonPath("$.pageable.pageNumber").isEqualTo(page)
                .jsonPath("$.pageable.pageSize").isEqualTo(size)
                .jsonPath("$.totalElements").isEqualTo(amount)
                .jsonPath("$.totalPages").isEqualTo((int) Math.ceil((double) amount / size))
                .jsonPath("$.first").isEqualTo(true)
                .jsonPath("$.last").isEqualTo(false);
    }

    @Test
    void getAllCategoriesPage3Asc() throws Exception {
        page = 3;

        defineSublist();

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
                .jsonPath("$.content.length()").isEqualTo(end - start)
                .jsonPath("$.content[0].id").isEqualTo("id-31")
                .jsonPath("$.content[0].name").isEqualTo("Category 31")
                .jsonPath("$.pageable.pageNumber").isEqualTo(page)
                .jsonPath("$.pageable.pageSize").isEqualTo(size)
                .jsonPath("$.totalElements").isEqualTo(amount)
                .jsonPath("$.totalPages").isEqualTo((int) Math.ceil((double) amount / size))
                .jsonPath("$.first").isEqualTo(false)
                .jsonPath("$.last").isEqualTo(page == (amount / size))
                .jsonPath("$.numberOfElements").isEqualTo(end - start);
    }

    private void defineSublist() {
        start = Math.min(page * size, categoryDtoList.size());
        end = Math.min((page + 1) * size, categoryDtoList.size());
        sublist = categoryDtoList.subList(start, end);

        when(categoryService.getAllCategories(any(Integer.class), any(Integer.class), any(String.class)))
                .thenReturn(Mono.just(new PageImpl<>(sublist, PageRequest.of(page, size), categoryDtoList.size())));
    }

}
