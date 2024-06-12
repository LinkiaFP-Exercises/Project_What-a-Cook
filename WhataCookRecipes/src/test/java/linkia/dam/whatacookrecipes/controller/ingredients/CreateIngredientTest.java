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
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@WebFluxTest(IngredientController.class)
@Import({IngredientService.class, CreateIngredientsComponent.class})
public class CreateIngredientTest {

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ingredientDto = generateIngredientDto();
    }

    @Test
    void createIngredient() {
        when(measureDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(measureDao.save(any(MeasureDto.class))).thenReturn(Mono.just(ingredientDto.getMeasure()));
        when(ingredientDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(ingredientDao.save(any(IngredientDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        verifyCreationIngredient();

        verify(measureDao, times(1)).findByNameIgnoreCase(anyString());
        verify(measureDao, times(1)).save(any(MeasureDto.class));
        verify(ingredientDao, times(1)).findByNameIgnoreCase(anyString());
        verify(ingredientDao, times(1)).save(any(IngredientDto.class));
    }

    @Test
    void createIngredientWithExistingMeasure() {
        when(measureDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.just(ingredientDto.getMeasure()));
        when(measureDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.just(ingredientDto.getMeasure()));
        when(ingredientDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(ingredientDao.save(any(IngredientDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        verifyCreationIngredient();

        verify(measureDao, times(1)).findByNameIgnoreCase(anyString());
        verify(measureDao, times(0)).save(any(MeasureDto.class));
        verify(ingredientDao, times(1)).findByNameIgnoreCase(anyString());
        verify(ingredientDao, times(1)).save(any(IngredientDto.class));
    }

    @Test
    void createIngredientAlreadyExists() {
        when(ingredientDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.just(ingredientDto));
        when(measureDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.just(ingredientDto.getMeasure()));

        verifyCreationIngredient();

        verify(measureDao, times(1)).findByNameIgnoreCase(anyString());
        verify(measureDao, times(0)).save(any(MeasureDto.class));
        verify(ingredientDao, times(1)).findByNameIgnoreCase(anyString());
        verify(ingredientDao, times(0)).save(any(IngredientDto.class));
    }
/*
    @SuppressWarnings("deprecation")
    @Test
    void createIngredientWithDuplicateKeyError() {
        when(measureDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(measureDao.save(any(MeasureDto.class))).thenReturn(Mono.just(ingredientDto.getMeasure()));
        when(ingredientDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(ingredientDao.save(any(IngredientDto.class)))
                .thenReturn(Mono.error(new MongoWriteException(new WriteError(11000, "Duplicate key error", new BsonDocument()), new ServerAddress())));

        StepVerifier.create(createIngredientsComponent.createIngredient(ingredientDto))
                .expectErrorMatches(throwable -> {
                    Throwable cause = throwable;
                    while (cause != null) {
                        if (cause instanceof MongoWriteException && cause.getMessage().contains("Duplicate key error")) {
                            return true;
                        }
                        cause = cause.getCause();
                    }
                    return false;
                })
                .verify();

        verify(measureDao, times(1)).findByNameIgnoreCase(anyString());
        verify(measureDao, times(1)).save(any(MeasureDto.class));
        verify(ingredientDao, times(1)).findByNameIgnoreCase(anyString());
        verify(ingredientDao, times(1)).save(any(IngredientDto.class));
    }
 */

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
