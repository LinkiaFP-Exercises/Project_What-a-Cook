package linkia.dam.whatacookrecipes.controller.ingredients;

import linkia.dam.whatacookrecipes.model.IngredientDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.when;

public class GetAllIngredientsTest extends BaseIngredientsTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        size = 10;
        when(ingredientDao.findAll()).thenReturn(Flux.fromIterable(ingredientDtoList));
    }

    private void validateResponse(String mode, IngredientDto expectedFirstIngredient, boolean isFirst, boolean isLast, int numberOfElements) {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(ingredientsUri)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("mode", mode)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(numberOfElements)
                .jsonPath("$.content[0].id").isEqualTo(expectedFirstIngredient.getId())
                .jsonPath("$.content[0].name").isEqualTo(expectedFirstIngredient.getName())
                .jsonPath("$.pageable.pageNumber").isEqualTo(page)
                .jsonPath("$.pageable.pageSize").isEqualTo(size)
                .jsonPath("$.totalElements").isEqualTo(amount)
                .jsonPath("$.totalPages").isEqualTo((int) Math.ceil((double) amount / size))
                .jsonPath("$.first").isEqualTo(isFirst)
                .jsonPath("$.last").isEqualTo(isLast);
    }

    @Test
    void getAllIngredientsPage0Asc() {
        page = 0;
        IngredientDto expectedFirstIngredient = getExpectedIngredientDto(false, null);

        validateResponse("", expectedFirstIngredient, true, false, size);
    }

    @Test
    void getAllIngredientsPage0Desc() {
        page = 0;
        IngredientDto expectedFirstIngredient = getExpectedIngredientDto(true, null);

        validateResponse("D", expectedFirstIngredient, true, false, size);
    }

    @Test
    void getAllIngredientsPage3Asc() {
        page = 3;
        IngredientDto expectedFirstIngredient = getExpectedIngredientDto(false, null);

        validateResponse("", expectedFirstIngredient, false, true, getNumberLastElements());
    }

    @Test
    void getAllIngredientsPage3Desc() {
        page = 3;
        IngredientDto expectedFirstIngredient = getExpectedIngredientDto(true, null);

        validateResponse("D", expectedFirstIngredient, false, true, getNumberLastElements());
    }
}
