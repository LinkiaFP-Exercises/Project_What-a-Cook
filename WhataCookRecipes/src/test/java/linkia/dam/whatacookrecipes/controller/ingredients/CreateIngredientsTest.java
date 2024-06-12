package linkia.dam.whatacookrecipes.controller.ingredients;

import linkia.dam.whatacookrecipes.controller.IngredientController;
import linkia.dam.whatacookrecipes.model.IngredientDto;
import linkia.dam.whatacookrecipes.model.MeasureDto;
import linkia.dam.whatacookrecipes.service.IngredientService;
import linkia.dam.whatacookrecipes.service.components.CreateIngredientsComponent;
import linkia.dam.whatacookrecipes.service.repository.IngredientDao;
import linkia.dam.whatacookrecipes.service.repository.MeasureDao;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@WebFluxTest(IngredientController.class)
@Import({IngredientService.class, CreateIngredientsComponent.class})
public class CreateIngredientsTest {

    @Autowired
    protected WebTestClient webTestClient;
    @Autowired
    protected IngredientService ingredientService;
    @MockBean
    protected IngredientDao ingredientDao;
    @Autowired
    protected CreateIngredientsComponent createIngredientsComponent;
    @MockBean
    private MeasureDao measureDao;
    @Value("${app.endpoint.ingredients}")
    protected String ingredientsUri;

    protected IngredientDto ingredientDto;

    protected IngredientDto generateIngredientDto() {
        MeasureDto cucharadita = new MeasureDto("measure-1", "Cucharadita");
        return new IngredientDto("ingredient-1", "Azúcar", 1.0, cucharadita);
    }

    private Flux<IngredientDto> ingredientDtoFlux;
    private final List<IngredientDto> ingredientDtoList = generateIngredientDtoList();
    private final int amount = ingredientDtoList.size();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ingredientDto = generateIngredientDto();
        ingredientDtoFlux = Flux.fromIterable(ingredientDtoList);
    }

    @Test
    void createIngredientsNew() {
        when(measureDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(measureDao.save(any(MeasureDto.class))).thenReturn(Mono.just(ingredientDto.getMeasure()));
        when(ingredientDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(ingredientDao.save(any(IngredientDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        testCreateIngredients();

        verify(ingredientDao, times(amount)).findByNameIgnoreCase(anyString());
        verify(ingredientDao, times(amount)).save(any(IngredientDto.class));
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
        when(measureDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.just(ingredientDto.getMeasure()));

        testCreateIngredients();

        verify(ingredientDao, times(amount)).findByNameIgnoreCase(anyString());
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
                    List<String> responseNames = ingredients.stream().map(IngredientDto::getName).toList();
                    assert responseNames.contains(ingredientDtoList.getFirst().getName());
                    assert responseNames.contains(ingredientDtoList.get(amount - 1).getName());
                });
    }

    public final  List<IngredientDto> generateIngredientDtoList() {
        MeasureDto cucharadita = new MeasureDto("measure-1", "Cucharadita");
        MeasureDto cucharada = new MeasureDto("measure-2", "Cucharada");
        MeasureDto taza = new MeasureDto("measure-3", "Taza");
        MeasureDto gramo = new MeasureDto("measure-11", "Gramo");
        MeasureDto kilogramo = new MeasureDto("measure-12", "Kilogramo");

        return Arrays.asList(
                new IngredientDto("ingredient-1", "Azúcar", 100.0, gramo),
                new IngredientDto("ingredient-2", "Sal", 200.0, gramo),
                new IngredientDto("ingredient-3", "Harina", 1.0, kilogramo),
                new IngredientDto("ingredient-4", "Leche", 2.0, taza),
                new IngredientDto("ingredient-5", "Aceite", 3.0, cucharada),
                new IngredientDto("ingredient-6", "Huevos", 4.0, cucharadita),
                new IngredientDto("ingredient-7", "Pollo", 1.5, kilogramo),
                new IngredientDto("ingredient-8", "Tomates", 2.5, kilogramo),
                new IngredientDto("ingredient-9", "Papas", 1.2, kilogramo),
                new IngredientDto("ingredient-10", "Cebollas", 1.0, kilogramo),
                new IngredientDto("ingredient-11", "Ajo", 5.0, cucharadita),
                new IngredientDto("ingredient-12", "Zanahorias", 0.5, kilogramo),
                new IngredientDto("ingredient-13", "Frijoles", 1.0, taza),
                new IngredientDto("ingredient-14", "Arroz", 2.0, taza),
                new IngredientDto("ingredient-15", "Lentejas", 3.0, taza),
                new IngredientDto("ingredient-16", "Pan", 6.0, cucharada),
                new IngredientDto("ingredient-17", "Mantequilla", 7.0, cucharada),
                new IngredientDto("ingredient-18", "Queso", 8.0, cucharada),
                new IngredientDto("ingredient-19", "Jamón", 9.0, cucharada),
                new IngredientDto("ingredient-20", "Tocino", 10.0, cucharada),
                new IngredientDto("ingredient-21", "Yogur", 11.0, cucharada),
                new IngredientDto("ingredient-22", "Crema", 12.0, cucharada),
                new IngredientDto("ingredient-23", "Café", 13.0, cucharada),
                new IngredientDto("ingredient-24", "Té", 14.0, cucharada),
                new IngredientDto("ingredient-25", "Agua", 15.0, taza),
                new IngredientDto("ingredient-26", "Vino", 16.0, taza),
                new IngredientDto("ingredient-27", "Cerveza", 17.0, taza),
                new IngredientDto("ingredient-28", "Jugo", 18.0, taza),
                new IngredientDto("ingredient-29", "Refresco", 19.0, taza),
                new IngredientDto("ingredient-30", "Miel", 20.0, cucharada),
                new IngredientDto("ingredient-31", "Canela", 21.0, cucharadita),
                new IngredientDto("ingredient-32", "Pimienta", 22.0, cucharadita),
                new IngredientDto("ingredient-33", "Perejil", 23.0, cucharadita),
                new IngredientDto("ingredient-34", "Orégano", 24.0, cucharadita),
                new IngredientDto("ingredient-35", "Tomillo", 25.0, cucharadita),
                new IngredientDto("ingredient-36", "Albahaca", 26.0, cucharadita)
        );
    }
}
