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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static linkia.dam.whatacookrecipes.controller.recipes.BaseRecipesTest.generateRecipeDtoListStatic;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.List;

@WebFluxTest(RecipeController.class)
@Import({RecipeService.class, CreateRecipesComponent.class})
public class CreateRecipesTest {

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
    @Value("${app.sub-endpoint.bulk}")
    protected String PATH_Bulk;

    private Flux<RecipeDto> recipeDtoFlux;
    private final List<RecipeDto> recipeDtoList = generateRecipeDtoListStatic();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        recipeDtoFlux = Flux.fromIterable(recipeDtoList);

    }

    @Test
    void createRecipesNew() {
        when(recipeDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(recipeDao.save(any(RecipeDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));
        when(ingredientDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(ingredientDao.save(any(IngredientDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));
        when(categoryDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(categoryDao.save(any(CategoryDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));
        when(measureDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(measureDao.save(any(MeasureDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        testCreateRecipes();

        verify(recipeDao, times(recipeDtoList.size())).save(any(RecipeDto.class));
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
                .uri(recipesUri + PATH_Bulk)
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
