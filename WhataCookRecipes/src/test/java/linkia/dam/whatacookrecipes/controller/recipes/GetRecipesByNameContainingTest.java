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
        pathVariable = recipesUri + PATH_ByName;
        size = 10;
        page = 0;
        queryParam = "name";
        queryParamValue = "c";

        recipeDtoListFiltered = recipeDtoList.stream()
                .filter(recipe -> recipe.getName().replace("Receta de ", "").contains(queryParamValue))
                .collect(Collectors.toList());

        when(recipeDao.findByNameContainingIgnoreCase(queryParamValue)).thenReturn(Flux.fromIterable(recipeDtoListFiltered));
    }

    private RecipeDto getExpectedRecipeDto(boolean desc) {
        return getExpectedRecipeDto(desc, recipeDtoListFiltered);
    }

    @Test
    void getRecipesByNameContainingAsc() {
        validateResponse(pathVariable,"", queryParam, queryParamValue,
                getExpectedRecipeDto(false), recipeDtoListFiltered);
    }

    @Test
    void getRecipesByNameContainingDesc() {
        validateResponse(pathVariable, "D", queryParam, queryParamValue,
                getExpectedRecipeDto(true), recipeDtoListFiltered);
    }
}
