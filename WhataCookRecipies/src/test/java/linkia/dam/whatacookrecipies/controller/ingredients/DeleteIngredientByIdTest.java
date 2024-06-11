package linkia.dam.whatacookrecipies.controller.ingredients;

import linkia.dam.whatacookrecipies.model.IngredientDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class DeleteIngredientByIdTest extends BaseIngredientsTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ingredientDto = generateIngredientDto();
        pathVariable = "/{id}";
        valuePathVariable = ingredientDto.getId();
    }

    @Test
    void testDeleteIngredientByIdFounded() {
        when(ingredientDao.findById(anyString())).thenReturn(Mono.just(ingredientDto));
        when(ingredientDao.delete(ingredientDto)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder.path(ingredientsUri + pathVariable)
                        .build(valuePathVariable))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> {
                    assert response.contains(ingredientDto.getName());
                    assert response.contains(DELETED);
                });
    }

    @Test
    void testDeleteIngredientByIdNotFound() {
        when(ingredientDao.findById(anyString())).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder.path(ingredientsUri + pathVariable)
                        .build(valuePathVariable))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

        verify(ingredientDao, times(0)).delete(any(IngredientDto.class));
    }
}
