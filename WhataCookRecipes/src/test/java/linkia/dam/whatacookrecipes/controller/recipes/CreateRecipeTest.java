package linkia.dam.whatacookrecipes.controller.recipes;

import linkia.dam.whatacookrecipes.controller.RecipeController;
import linkia.dam.whatacookrecipes.model.CategoryDto;
import linkia.dam.whatacookrecipes.model.IngredientDto;
import linkia.dam.whatacookrecipes.model.MeasureDto;
import linkia.dam.whatacookrecipes.model.RecipeDto;
import linkia.dam.whatacookrecipes.service.RecipeService;
import linkia.dam.whatacookrecipes.service.components.CreateRecipesComponent;
import linkia.dam.whatacookrecipes.service.repository.CategoryDao;
import linkia.dam.whatacookrecipes.service.repository.IngredientDao;
import linkia.dam.whatacookrecipes.service.repository.MeasureDao;
import linkia.dam.whatacookrecipes.service.repository.RecipeDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static linkia.dam.whatacookrecipes.controller.recipes.BaseRecipesTest.generateRecipeDtoStatic;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@WebFluxTest(RecipeController.class)
@Import({RecipeService.class, CreateRecipesComponent.class})
public class CreateRecipeTest {

    @Autowired
    protected WebTestClient webTestClient;
    @MockBean
    protected RecipeDao recipeDao;
    @MockBean
    protected IngredientDao ingredientDao;
    @MockBean
    protected CategoryDao categoryDao;
    @MockBean
    protected MeasureDao measureDao;
    @Autowired
    protected CreateRecipesComponent createRecipesComponent;

    @Value("${app.endpoint.recipes}")
    protected String recipesUri;

    protected RecipeDto recipeDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        recipeDto = generateRecipeDtoStatic();
    }

    @Test
    void createRecipe() {
        when(recipeDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(recipeDao.save(any(RecipeDto.class))).thenReturn(Mono.just(recipeDto));
        when(ingredientDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(ingredientDao.save(any(IngredientDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));
        when(categoryDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(categoryDao.save(any(CategoryDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));
        when(measureDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(measureDao.save(any(MeasureDto.class))).thenReturn(Mono.just(recipeDto.getIngredients().getFirst().getMeasure()));

        verifyCreationRecipe();

        verify(recipeDao, times(1)).save(any(RecipeDto.class));
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
