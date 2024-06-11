package linkia.dam.whatacookrecipes.controller.ingredients;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GetIngredientByIdTest extends BaseIngredientsTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ingredientDto = generateIngredientDto();
        pathVariable = ingredientsUri + PATH_SLASH_ID;
        valuePathVariable = ingredientDto.getId();
    }

    @Test
    void getIngredientByIdFounded() {
        when(ingredientDao.findById(anyString())).thenReturn(Mono.just(ingredientDto));
        TestGetByPathVariableFounded(webTestClient, pathVariable, valuePathVariable, ingredientDto);
    }

    @Test
    void getIngredientByIdNotFound() {
        when(ingredientDao.findById(anyString())).thenReturn(Mono.empty());
        TestGetByPathVariableNotFound(webTestClient, pathVariable, valuePathVariable);
    }

}
