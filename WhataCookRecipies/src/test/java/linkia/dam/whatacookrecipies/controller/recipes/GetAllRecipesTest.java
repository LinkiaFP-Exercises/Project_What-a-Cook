package linkia.dam.whatacookrecipies.controller.recipes;

import linkia.dam.whatacookrecipies.model.RecipeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import static org.mockito.Mockito.when;

public class GetAllRecipesTest extends BaseRecipesTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        size = 10;
        when(recipeDao.findAll()).thenReturn(Flux.fromIterable(recipeDtoList));
    }

    private void validateResponse(String mode, RecipeDto expectedFirstRecipe, boolean isFirst, boolean isLast, int numberOfElements) {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(recipesUri)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("mode", mode)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(numberOfElements)
                .jsonPath("$.content[0].id").isEqualTo(expectedFirstRecipe.getId())
                .jsonPath("$.content[0].name").isEqualTo(expectedFirstRecipe.getName())
                .jsonPath("$.pageable.pageNumber").isEqualTo(page)
                .jsonPath("$.pageable.pageSize").isEqualTo(size)
                .jsonPath("$.totalElements").isEqualTo(amount)
                .jsonPath("$.totalPages").isEqualTo((int) Math.ceil((double) amount / size))
                .jsonPath("$.first").isEqualTo(isFirst)
                .jsonPath("$.last").isEqualTo(isLast);
    }

    @Test
    void getAllRecipesPage0Asc() {
        page = 0;
        RecipeDto expectedFirstRecipe = getExpectedRecipeDto(false, null);

        validateResponse("", expectedFirstRecipe, true, false, size);
    }

    @Test
    void getAllRecipesPage0Desc() {
        page = 0;
        RecipeDto expectedFirstRecipe = getExpectedRecipeDto(true, null);

        validateResponse("D", expectedFirstRecipe, true, false, size);
    }

    @Test
    void getAllRecipesPage3Asc() {
        page = 3;
        RecipeDto expectedFirstRecipe = getExpectedRecipeDto(false, null);

        validateResponse("", expectedFirstRecipe, false, true, getNumberLastElements());
    }

    @Test
    void getAllRecipesPage3Desc() {
        page = 3;
        RecipeDto expectedFirstRecipe = getExpectedRecipeDto(true, null);

        validateResponse("D", expectedFirstRecipe, false, true, getNumberLastElements());
    }
}
