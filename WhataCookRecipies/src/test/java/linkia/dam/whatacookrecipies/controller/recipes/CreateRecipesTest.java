package linkia.dam.whatacookrecipies.controller.recipes;

import linkia.dam.whatacookrecipies.model.RecipeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CreateRecipesTest extends BaseRecipesTest {

    private Flux<RecipeDto> recipeDtoFlux;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        recipeDtoFlux = Flux.fromIterable(recipeDtoList);
    }

    @Test
    void createRecipesNew() {
        when(recipeDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(recipeDao.save(any(RecipeDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        testCreateRecipes();
    }

    @Test
    void createRecipesExistingRecipes() {
        when(recipeDao.findByNameIgnoreCase(anyString())).thenAnswer(invocation -> {
            String name = invocation.getArgument(0);
            return recipeDtoList.stream()
                    .filter(recipeDto -> recipeDto.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .map(Mono::just)
                    .orElse(Mono.empty());
        });

        testCreateRecipes();

        verify(recipeDao, times(0)).save(any(RecipeDto.class));
    }

    private void testCreateRecipes() {
        webTestClient.post()
                .uri(recipesUri + "/bulk")
                .body(recipeDtoFlux, RecipeDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(RecipeDto.class)
                .hasSize(amount)
                .value(recipes -> {
                    List<String> responseNames = recipes.stream().map(RecipeDto::getName).toList();
                    assert responseNames.contains(recipeDtoList.getFirst().getName());
                    assert responseNames.contains(recipeDtoList.get(amount - 1).getName());
                });
    }

}
