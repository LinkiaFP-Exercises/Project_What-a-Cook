package linkia.dam.whatacookrecipies.controller.recipes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GetRecipeByNameTest extends BaseRecipesTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        recipeDto = generateRecipeDto();
        pathVariable = recipesUri + PATH_NAME;
        valuePathVariable = recipeDto.getName();
    }

    @Test
    void getRecipeByNameFound() {
        when(recipeDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.just(recipeDto));
        TestGetByPathVariableFounded(webTestClient, pathVariable, valuePathVariable, recipeDto);
    }

    @Test
    void getRecipeByNameNotFound() {
        when(recipeDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        TestGetByPathVariableNotFound(webTestClient, pathVariable, valuePathVariable);
    }

}
