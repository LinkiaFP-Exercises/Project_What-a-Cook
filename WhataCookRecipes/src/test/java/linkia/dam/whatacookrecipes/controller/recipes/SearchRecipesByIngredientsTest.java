package linkia.dam.whatacookrecipes.controller.recipes;

import linkia.dam.whatacookrecipes.model.RecipeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

public class SearchRecipesByIngredientsTest extends BaseRecipesTest {

    public List<RecipeDto> recipeDtoListFiltered;
    private List<String> ingredientNames;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ingredientNames = List.of(ingredientDtoList.getFirst().getName(), ingredientDtoList.get(11).getName());
        size = 10;


        recipeDtoListFiltered = recipeDtoList.stream()
                .filter(recipe -> ingredientNames.stream()
                        .allMatch(ingredientName -> recipe.getIngredients().stream()
                                .anyMatch(ingredient -> ingredient.getName().equalsIgnoreCase(ingredientName))))
                .collect(Collectors.toList());

        System.out.println("Ingredientes seleccionados: " + ingredientNames);
        recipeDtoList.forEach(recipe -> {
            System.out.println("Receta: " + recipe.getName());
            recipe.getIngredients().forEach(ingredient -> System.out.println("Ingrediente: " + ingredient.getName()));
        });
        System.out.println("Recetas filtradas: " + recipeDtoListFiltered.size());

        when(recipeDao.findByIngredientsNameIn(ingredientNames)).thenReturn(Flux.fromIterable(recipeDtoListFiltered));
    }

    private void validateResponse(String mode, List<String> ingredients, RecipeDto expectedFirstRecipe, int numberOfElements) {
        var exchangeResult = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(recipesUri + PATH_ByIngredients)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("mode", mode)
                        .queryParam("ingredients", String.join(",", ingredients))
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(numberOfElements);

        if (numberOfElements > 0) {
            exchangeResult
                    .jsonPath("$.content[0].id").isEqualTo(expectedFirstRecipe.getId())
                    .jsonPath("$.content[0].name").isEqualTo(expectedFirstRecipe.getName());
        }

        exchangeResult
                .jsonPath("$.pageable.pageNumber").isEqualTo(page)
                .jsonPath("$.pageable.pageSize").isEqualTo(size)
                .jsonPath("$.totalElements").isEqualTo(recipeDtoListFiltered.size())
                .jsonPath("$.totalPages").isEqualTo((int) Math.ceil((double) recipeDtoListFiltered.size() / size))
                .jsonPath("$.first").isEqualTo(true)
                .jsonPath("$.last").isEqualTo(recipeDtoListFiltered.size() <= size);
    }

    @Test
    void getRecipesByIngredientsAsc() {
        page = 0;
        RecipeDto expectedFirstRecipe = null;
        if (!recipeDtoListFiltered.isEmpty()) {
            expectedFirstRecipe = getExpectedRecipeDto(false, recipeDtoListFiltered);
        }
        validateResponse("", ingredientNames, expectedFirstRecipe, recipeDtoListFiltered.size());
    }

    @Test
    void getRecipesByIngredientsDesc() {
        page = 0;
        RecipeDto expectedFirstRecipe = null;
        if (!recipeDtoListFiltered.isEmpty()) {
            expectedFirstRecipe = getExpectedRecipeDto(true, recipeDtoListFiltered);
        }
        validateResponse("D", ingredientNames, expectedFirstRecipe, recipeDtoListFiltered.size());
    }
}
