package linkia.dam.whatacookrecipies.controller.ingredients;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GetIngredientByNameTest extends BaseIngredientsTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ingredientDto = generateIngredientDto();
        pathVariable = ingredientsUri + PATH_NAME;
        valuePathVariable = ingredientDto.getName();
    }

    @Test
    void getIngredientByNameFound() {
        when(ingredientDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.just(ingredientDto));
        TestGetByPathVariableFounded(webTestClient, pathVariable, valuePathVariable, ingredientDto);
    }

    @Test
    void getIngredientByNameNotFound() {
        when(ingredientDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        TestGetByPathVariableNotFound(webTestClient, pathVariable, valuePathVariable);
    }

}
