package linkia.dam.whatacookrecipes.controller.recipes;

import linkia.dam.whatacookrecipes.model.CategoryDto;
import linkia.dam.whatacookrecipes.model.IngredientDto;
import linkia.dam.whatacookrecipes.model.RecipeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

public class CreateRecipeTest extends BaseRecipesTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        recipeDto = generateRecipeDto();
        when(ingredientService.createIngredient(any(IngredientDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));
        when(categoryService.createCategory(any(CategoryDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));
    }

    @Test
    void createRecipe() {
        when(recipeDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(recipeDao.save(any(RecipeDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        verifyCreationRecipe();

        verify(recipeDao, times(1)).save(any(RecipeDto.class));
        verify(ingredientService, times(recipeDto.getIngredients().size())).createIngredient(any(IngredientDto.class));
        verify(categoryService, times(recipeDto.getCategories().size())).createCategory(any(CategoryDto.class));
    }

    @Test
    void createRecipeAlreadyExists() {
        when(recipeDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.just(recipeDto));

        verifyCreationRecipe();

        verify(recipeDao, times(0)).save(any(RecipeDto.class));
    }

    private void verifyCreationRecipe() {
        webTestClient.post()
                .uri(recipesUri)
                .contentType(APPLICATION_JSON)
                .body(fromValue(recipeDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(recipeDto.getId())
                .jsonPath("$.name").isEqualTo(recipeDto.getName());
    }
}
