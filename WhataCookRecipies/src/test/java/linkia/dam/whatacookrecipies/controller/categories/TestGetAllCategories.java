package linkia.dam.whatacookrecipies.controller.categories;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import linkia.dam.whatacookrecipies.model.CategoryDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class TestGetAllCategories extends BaseCategoriesTest {

    private int page;
    private int size;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        page = 0;
        size = 10;
        amount = 36;
        categoryDtoList = generateCategoryDtoList(amount);

        List<CategoryDto> pageContent = categoryDtoList.subList(page * size, Math.min((page + 1) * size, categoryDtoList.size()));
        when(categoryService.getAllCategories(any(Integer.class), any(Integer.class), any(String.class)))
                .thenReturn(Mono.just(new PageImpl<>(pageContent, PageRequest.of(page, size), categoryDtoList.size())));
    }
    @Test
    void getAllCategories1() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(categoriesUri)
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(10)
                .jsonPath("$.content[?(@.id == 'id-1')]").exists()
                .jsonPath("$.pageable.pageNumber").isEqualTo(0)
                .jsonPath("$.pageable.pageSize").isEqualTo(10)
                .jsonPath("$.last").isEqualTo(false)
                .jsonPath("$.first").isEqualTo(true);
    }

    @Test
    void getAllCategories() throws Exception {
        String responseBody = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(categoriesUri)
                        .queryParam("page", 0)
                        .queryParam("size", 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .returnResult(String.class)
                .getResponseBody()
                .blockFirst();

        System.out.println("Response Body: " + responseBody);
        assertNotNull(responseBody, "The response body is null");

        // Deserialize JSON response to PageImpl<CategoryDto>
        final JavaType type = objectMapper.getTypeFactory().constructParametricType(PageImpl.class, CategoryDto.class);
        final PageImpl<CategoryDto> resultPage = objectMapper.readValue(responseBody, type);

        assertAll(
                () -> assertNotNull(resultPage),
                () -> assertEquals(resultPage.getContent().size(), size),
                () -> assertSame("id-1", resultPage.getContent().getFirst().getId()),
                () -> assertEquals(resultPage.getPageable().getPageNumber(), page),
                () -> assertEquals(resultPage.getPageable().getPageSize(), size),
                () -> assertEquals(resultPage.getTotalPages(), (int) (double) (amount / size)),
                () -> assertEquals(resultPage.getTotalElements(), amount),
                () -> assertTrue(resultPage.isFirst()),
                () -> assertFalse(resultPage.isLast())
        );
    }
}
