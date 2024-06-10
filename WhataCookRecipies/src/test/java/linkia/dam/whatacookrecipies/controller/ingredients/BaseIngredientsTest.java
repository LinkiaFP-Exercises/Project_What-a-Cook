package linkia.dam.whatacookrecipies.controller.ingredients;

import linkia.dam.whatacookrecipies.controller.BaseTestingConfiguration;
import linkia.dam.whatacookrecipies.controller.IngredientController;
import linkia.dam.whatacookrecipies.model.IngredientDto;
import linkia.dam.whatacookrecipies.model.IngredientDto;
import linkia.dam.whatacookrecipies.model.MeasureDto;
import linkia.dam.whatacookrecipies.service.IngredientService;
import linkia.dam.whatacookrecipies.service.contracts.IngredientDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@WebFluxTest(IngredientController.class)
@Import(IngredientService.class)
public class BaseIngredientsTest extends BaseTestingConfiguration {

    @Autowired
    protected WebTestClient webTestClient;
    @Autowired
    protected IngredientService ingredientService;
    @MockBean
    protected IngredientDao ingredientDao;
    @Value("${app.endpoint.ingredients}")
    protected String ingredientsUri;

    protected int page;
    protected int size;
    protected String name;
    public final List<IngredientDto> ingredientDtoList = generateIngredientDtoList();
    protected IngredientDto ingredientDto;

    public final  List<IngredientDto> generateIngredientDtoList() {
        MeasureDto cucharadita = new MeasureDto("measure-1", "Cucharadita");
        MeasureDto cucharada = new MeasureDto("measure-2", "Cucharada");
        MeasureDto taza = new MeasureDto("measure-3", "Taza");
        MeasureDto gramo = new MeasureDto("measure-11", "Gramo");
        MeasureDto kilogramo = new MeasureDto("measure-12", "Kilogramo");

        List<IngredientDto> ingredientes = Arrays.asList(
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
        return ingredientes;
    }

    protected IngredientDto getExpectedIngredientDto(boolean desc, List<IngredientDto> otherIngredientDtoList) {
        List<IngredientDto> listToSort = (otherIngredientDtoList == null) ? ingredientDtoList : otherIngredientDtoList;
        return getExpectedDto(desc, listToSort);
    }

    protected IngredientDto generateIngredientDto() {
        MeasureDto cucharadita = new MeasureDto("measure-1", "Cucharadita");
        return new IngredientDto("ingredient-1", "Azúcar", 1.0, cucharadita);
    }
}
