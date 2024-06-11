package linkia.dam.whatacookrecipes.controller.recipes;

import linkia.dam.whatacookrecipes.model.RecipeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

public class GetRecipesByNameContainingTest extends BaseRecipesTest {

    public List<RecipeDto> recipeDtoListFiltered;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        name = "c";
        size = 10;

        recipeDtoListFiltered = recipeDtoList.stream()
                .filter(recipe -> recipe.getName().replace("Receta de ", "").contains(name))
                .collect(Collectors.toList());

        when(recipeDao.findByNameContainingIgnoreCase(name)).thenReturn(Flux.fromIterable(recipeDtoListFiltered));
    }

    private void validateResponse(String mode, String name, RecipeDto expectedFirstRecipe, int numberOfElements) {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(recipesUri + "/searchPaged")
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
                .jsonPath("$.content[0].id").isEqualTo(expectedFirstRecipe.getId())
                .jsonPath("$.content[0].name").isEqualTo(expectedFirstRecipe.getName())
                .jsonPath("$.pageable.pageNumber").isEqualTo(page)
                .jsonPath("$.pageable.pageSize").isEqualTo(size)
                .jsonPath("$.totalElements").isEqualTo(recipeDtoListFiltered.size())
                .jsonPath("$.totalPages").isEqualTo((int) Math.ceil((double) recipeDtoListFiltered.size() / size))
                .jsonPath("$.first").isEqualTo(true)
                .jsonPath("$.last").isEqualTo(true);
    }

    @Test
    void getRecipesByNameContainingAsc() {
        page = 0;
        RecipeDto expectedFirstRecipe = getExpectedRecipeDto(false, recipeDtoListFiltered);

        validateResponse("", name, expectedFirstRecipe, recipeDtoListFiltered.size());
    }

    @Test
    void getRecipesByNameContainingDesc() {
        page = 0;
        RecipeDto expectedFirstRecipe = getExpectedRecipeDto(true, recipeDtoListFiltered);

        validateResponse("D", name, expectedFirstRecipe, recipeDtoListFiltered.size());
    }
}
