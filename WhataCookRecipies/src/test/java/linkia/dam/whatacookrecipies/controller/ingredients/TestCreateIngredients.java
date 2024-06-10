package linkia.dam.whatacookrecipies.controller.ingredients;

import linkia.dam.whatacookrecipies.model.IngredientDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TestCreateIngredients extends BaseIngredientsTest {

    private Flux<IngredientDto> ingredientDtoFlux;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ingredientDtoFlux = Flux.fromIterable(ingredientDtoList);
    }

    @Test
    void createIngredientsNew() {
        when(ingredientDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(ingredientDao.save(any(IngredientDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        testCreateIngredients();
    }

    @Test
    void createIngredientsExistingIngredients() {
        when(ingredientDao.findByNameIgnoreCase(anyString())).thenAnswer(invocation -> {
            String name = invocation.getArgument(0);
            return ingredientDtoList.stream()
                    .filter(ingredientDto -> ingredientDto.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .map(Mono::just)
                    .orElse(Mono.empty());
        });

        testCreateIngredients();

        verify(ingredientDao, times(0)).save(any(IngredientDto.class));
    }

    private void testCreateIngredients() {
        webTestClient.post()
                .uri(ingredientsUri + "/bulk")
                .body(ingredientDtoFlux, IngredientDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(IngredientDto.class)
                .hasSize(amount)
                .value(ingredients -> {
                    List<String> responseNames = ingredients.stream().map(IngredientDto::getName).collect(Collectors.toList());
                    assert responseNames.contains(ingredientDtoList.get(0).getName());
                    assert responseNames.contains(ingredientDtoList.get(amount - 1).getName());
                });
    }

}
