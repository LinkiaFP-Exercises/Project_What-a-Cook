package linkia.dam.whatacookrecipes.controller.recipes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GetRecipeByIdTest extends BaseRecipesTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        recipeDto = generateRecipeDto();
        pathVariable = recipesUri + PATH_ID;
        valuePathVariable = recipeDto.getId();
    }

    @Test
    void getRecipeByIdFound() {
        when(recipeDao.findById(anyString())).thenReturn(Mono.just(recipeDto));
        TestGetByPathVariableFounded(webTestClient, pathVariable, valuePathVariable, recipeDto);
    }

    @Test
    void getRecipeByIdNotFound() {
        when(recipeDao.findById(anyString())).thenReturn(Mono.empty());
        TestGetByPathVariableNotFound(webTestClient, pathVariable, valuePathVariable);
    }

}
