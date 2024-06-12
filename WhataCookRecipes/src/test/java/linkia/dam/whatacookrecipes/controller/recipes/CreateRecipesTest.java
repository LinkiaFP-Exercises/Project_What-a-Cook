package linkia.dam.whatacookrecipes.controller.recipes;

import linkia.dam.whatacookrecipes.model.CategoryDto;
import linkia.dam.whatacookrecipes.model.IngredientDto;
import linkia.dam.whatacookrecipes.model.RecipeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;

public class CreateRecipesTest extends BaseRecipesTest {

    private Flux<RecipeDto> recipeDtoFlux;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        recipeDtoFlux = Flux.fromIterable(recipeDtoList);
        when(ingredientService.createIngredient(any(IngredientDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));
        when(categoryService.createCategory(any(CategoryDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));
    }

    @Test
    void createRecipesNew() {
        when(recipeDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(recipeDao.save(any(RecipeDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        testCreateRecipes();

        verify(recipeDao, times(recipeDtoList.size())).save(any(RecipeDto.class));
        verify(ingredientService, times(recipeDtoList.stream().mapToInt(r -> r.getIngredients().size()).sum())).createIngredient(any(IngredientDto.class));
        verify(categoryService, times(recipeDtoList.stream().mapToInt(r -> r.getCategories().size()).sum())).createCategory(any(CategoryDto.class));
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
                .hasSize(recipeDtoList.size())
                .value(recipes -> {
                    List<String> responseNames = recipes.stream().map(RecipeDto::getName).toList();
                    assert responseNames.contains(recipeDtoList.getFirst().getName());
                    assert responseNames.contains(recipeDtoList.getLast().getName());
                });
    }
}
