package linkia.dam.whatacookrecipes.controller.ingredients;

import linkia.dam.whatacookrecipes.model.IngredientDto;
import linkia.dam.whatacookrecipes.model.MeasureDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

public class CreateIngredientTest extends BaseIngredientsTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ingredientDto = generateIngredientDto();
        when(measureService.createMeasure(any(MeasureDto.class))).thenReturn(Mono.just(ingredientDto.getMeasure()));
    }

    @Test
    void createIngredient() {
        when(ingredientDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(ingredientDao.save(any(IngredientDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));


        verifyCreationIngredient();

        verify(ingredientDao, times(1)).save(any(IngredientDto.class));

    }

    @Test
    void createIngredientAlreadyExists() {
        when(ingredientDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.just(ingredientDto));

        verifyCreationIngredient();

        verify(ingredientDao, times(0)).save(any(IngredientDto.class));
        verify(measureService, times(1)).createMeasure(any(MeasureDto.class));
    }

    private void verifyCreationIngredient() {
        webTestClient.post()
                .uri(ingredientsUri)
                .contentType(APPLICATION_JSON)
                .body(fromValue(ingredientDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(ingredientDto.getId())
                .jsonPath("$.name").isEqualTo(ingredientDto.getName());
    }
}
