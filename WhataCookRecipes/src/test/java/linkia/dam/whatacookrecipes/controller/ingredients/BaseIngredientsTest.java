package linkia.dam.whatacookrecipes.controller.ingredients;

import linkia.dam.whatacookrecipes.controller.BaseConfigurationTest;
import linkia.dam.whatacookrecipes.controller.IngredientController;
import linkia.dam.whatacookrecipes.model.IngredientDto;
import linkia.dam.whatacookrecipes.model.MeasureDto;
import linkia.dam.whatacookrecipes.service.IngredientService;
import linkia.dam.whatacookrecipes.service.contracts.IngredientDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@WebFluxTest(IngredientController.class)
@Import(IngredientService.class)
public class BaseIngredientsTest extends BaseConfigurationTest {

    @Autowired
    protected WebTestClient webTestClient;
    @Autowired
    protected IngredientService ingredientService;
    @MockBean
    protected IngredientDao ingredientDao;
    @Value("${app.endpoint.ingredients}")
    protected String ingredientsUri;

    protected IngredientDto ingredientDto;

    protected IngredientDto getExpectedIngredientDto(boolean desc, List<IngredientDto> otherIngredientDtoList) {
        List<IngredientDto> listToSort = (otherIngredientDtoList == null) ? ingredientDtoList : otherIngredientDtoList;
        return getExpectedDto(desc, listToSort);
    }

    protected IngredientDto generateIngredientDto() {
        MeasureDto cucharadita = new MeasureDto("measure-1", "Cucharadita");
        return new IngredientDto("ingredient-1", "Azúcar", 1.0, cucharadita);
    }
}
