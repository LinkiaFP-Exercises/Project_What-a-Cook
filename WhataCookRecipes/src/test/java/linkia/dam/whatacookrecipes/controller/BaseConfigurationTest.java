package linkia.dam.whatacookrecipes.controller;

import linkia.dam.whatacookrecipes.model.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(SpringExtension.class)
public class BaseConfigurationTest {

    @Value("${app.sub-endpoint.id.path-variable-id}")
    protected String PATH_ID;
    @Value("${app.sub-endpoint.name.path-variable-name}")
    protected String PATH_NAME;
    @Value("${app.sub-endpoint.path-variable-id}")
    protected String PATH_VARIABLE_ID;
    @Value("${app.sub-endpoint.by-name}")
    protected String PATH_ByName;
    @Value("${app.sub-endpoint.by-ingredients}")
    protected String PATH_ByIngredients;
    @Value("${app.sub-endpoint.by-all-ingredients}")
    protected String PATH_ByAllIngredients;
    @Value("${app.sub-endpoint.bulk}")
    protected String PATH_Bulk;
    @Value("${app.sub-endpoint.all}")
    protected String PATH_All;

    protected static String pathVariable, valuePathVariable, name;
    protected static int page, size, amount = 36;
    protected static final String DELETED = "deleted";
    public final List<IngredientDto> ingredientDtoList = generateIngredientDtoList();

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

    protected int getNumberLastElements() {
        return amount % size == 0 ? size : amount % size;
    }

    protected <T extends NamedEntity> T getExpectedDto(boolean desc, List<T> listToSort) {
        List<T> sortedList = new ArrayList<>(listToSort);

        sortedList.sort((a, b) -> desc ? b.getName().compareTo(a.getName()) : a.getName().compareTo(b.getName()));

        int startIndex = page * size;
//        if (startIndex >= sortedList.size() || startIndex < 0) {
//            throw new IndexOutOfBoundsException("Start index is out of bounds");
//        }

        return sortedList.get(startIndex);
    }

    protected void TestGetByPathVariableFounded(WebTestClient webTestClient, String pathVariable, String valuePathVariable, NamedEntity namedEntity) {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(pathVariable)
                        .build(valuePathVariable))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(namedEntity.getId())
                .jsonPath("$.name").isEqualTo(namedEntity.getName());
    }

    protected void TestGetByPathVariableNotFound(WebTestClient webTestClient, String pathVariable, String valuePathVariable) {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(pathVariable)
                        .build(valuePathVariable))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

}
