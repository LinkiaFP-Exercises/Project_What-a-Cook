package linkia.dam.whatacookrecipes.controller.recipes;

import linkia.dam.whatacookrecipes.model.IngredientDto;
import linkia.dam.whatacookrecipes.model.RecipeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.mockito.Mockito.when;

public class SearchRecipesByAllIngredientsTest extends BaseRecipesTest {

    public List<RecipeDto> recipeDtoListFiltered;
    private List<IngredientDto> ingredientSelected;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pathVariable = recipesUri + PATH_ByAllIngredients;
        size = 10;
        page = 0;

        ingredientSelected = List.of(ingredientDtoList.getFirst(), ingredientDtoList.get(14)); // Azúcar, Arroz
        List<String> ingredientNames = ingredientSelected.stream().map(IngredientDto::getName).toList();
        queryParam = "ingredients";
        queryParamValue = String.join(",", ingredientNames);

        recipeDtoListFiltered = recipeDtoList.stream()
                .filter(recipe -> recipe.getIngredients().contains(ingredientSelected.getFirst())
                        && recipe.getIngredients().contains(ingredientSelected.getLast()))
                .toList();

        when(recipeDao.findByAllIngredientsNameIn(ingredientNames)).thenReturn(Flux.fromIterable(recipeDtoListFiltered));
    }

    private RecipeDto getExpectedRecipeDto(boolean desc) {
        return getExpectedRecipeDto(desc, recipeDtoListFiltered);
    }

    @Test
    void getRecipesByIngredientsAsc() {
        validateResponse(pathVariable,"", queryParam, queryParamValue,
                getExpectedRecipeDto(false), recipeDtoListFiltered);
    }

    @Test
    void getRecipesByIngredientsDesc() {
        validateResponse(pathVariable, "D", queryParam, queryParamValue,
                getExpectedRecipeDto(true), recipeDtoListFiltered);
    }
}
