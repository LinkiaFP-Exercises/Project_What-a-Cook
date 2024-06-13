package linkia.dam.whatacookrecipes.controller.recipes;

import linkia.dam.whatacookrecipes.controller.BaseConfigurationTest;
import linkia.dam.whatacookrecipes.controller.RecipeController;
import linkia.dam.whatacookrecipes.model.CategoryDto;
import linkia.dam.whatacookrecipes.model.RecipeDto;
import linkia.dam.whatacookrecipes.service.RecipeService;
import linkia.dam.whatacookrecipes.service.components.CreateRecipesComponent;
import linkia.dam.whatacookrecipes.service.repository.RecipeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.List;

@WebFluxTest(RecipeController.class)
@Import(RecipeService.class)
public class BaseRecipesTest extends BaseConfigurationTest {

    @Autowired
    protected WebTestClient webTestClient;
    @Autowired
    protected RecipeService recipeService;
    @MockBean
    protected RecipeDao recipeDao;
    @MockBean
    protected CreateRecipesComponent createRecipesComponent;

    @Value("${app.endpoint.recipes}")
    protected String recipesUri;

    public final List<RecipeDto> recipeDtoList = generateRecipeDtoList();
    protected RecipeDto recipeDto;

    public List<RecipeDto> generateRecipeDtoList() {
        CategoryDto categoria1 = new CategoryDto("category-1", "Desayuno");
        CategoryDto categoria2 = new CategoryDto("category-2", "Almuerzo");
        CategoryDto categoria3 = new CategoryDto("category-3", "Cena");
        CategoryDto categoria4 = new CategoryDto("category-4", "Postre");

        return Arrays.asList(
                new RecipeDto("recipe-1", "Receta de Azúcar", List.of(ingredientDtoList.getFirst(), ingredientDtoList.get(10)), List.of(categoria4), "Preparación de receta de azúcar", 4),
                new RecipeDto("recipe-2", "Receta de Sal", List.of(ingredientDtoList.get(1), ingredientDtoList.get(11)), List.of(categoria2), "Preparación de receta de sal", 2),
                new RecipeDto("recipe-3", "Receta de Harina", List.of(ingredientDtoList.get(2), ingredientDtoList.get(12)), List.of(categoria1), "Preparación de receta de harina", 6),
                new RecipeDto("recipe-4", "Receta de Leche", List.of(ingredientDtoList.get(3), ingredientDtoList.get(13)), List.of(categoria3), "Preparación de receta de leche", 1),
                new RecipeDto("recipe-5", "Receta de Aceite", List.of(ingredientDtoList.get(4), ingredientDtoList.get(14)), List.of(categoria4), "Preparación de receta de aceite", 3),
                new RecipeDto("recipe-6", "Receta de Huevos", List.of(ingredientDtoList.get(5), ingredientDtoList.get(15)), List.of(categoria2), "Preparación de receta de huevos", 5),
                new RecipeDto("recipe-7", "Receta de Pollo", List.of(ingredientDtoList.get(6), ingredientDtoList.get(16)), List.of(categoria1), "Preparación de receta de pollo", 4),
                new RecipeDto("recipe-8", "Receta de Tomates", List.of(ingredientDtoList.get(7), ingredientDtoList.get(17)), List.of(categoria3), "Preparación de receta de tomates", 2),
                new RecipeDto("recipe-9", "Receta de Papas", List.of(ingredientDtoList.get(8), ingredientDtoList.get(18)), List.of(categoria4), "Preparación de receta de papas", 7),
                new RecipeDto("recipe-10", "Receta de Cebollas", List.of(ingredientDtoList.get(9), ingredientDtoList.get(19)), List.of(categoria2), "Preparación de receta de cebollas", 3),
                new RecipeDto("recipe-11", "Receta de Frijoles", List.of(ingredientDtoList.get(12), ingredientDtoList.get(1)), List.of(categoria1), "Preparación de receta de frijoles", 8),
                new RecipeDto("recipe-12", "Receta de Arroz", List.of(ingredientDtoList.get(13), ingredientDtoList.get(3)), List.of(categoria3), "Preparación de receta de arroz", 6),
                new RecipeDto("recipe-13", "Receta de Lentejas", List.of(ingredientDtoList.get(14), ingredientDtoList.getFirst()), List.of(categoria4), "Preparación de receta de lentejas", 5),
                new RecipeDto("recipe-14", "Receta de Pan", List.of(ingredientDtoList.get(15), ingredientDtoList.get(2)), List.of(categoria2), "Preparación de receta de pan", 2),
                new RecipeDto("recipe-15", "Receta de Mantequilla", List.of(ingredientDtoList.get(16), ingredientDtoList.get(4)), List.of(categoria1), "Preparación de receta de mantequilla", 3),
                new RecipeDto("recipe-16", "Receta de Queso", List.of(ingredientDtoList.get(17), ingredientDtoList.get(5)), List.of(categoria3), "Preparación de receta de queso", 1),
                new RecipeDto("recipe-17", "Receta de Jamón", List.of(ingredientDtoList.get(18), ingredientDtoList.get(6)), List.of(categoria4), "Preparación de receta de jamón", 2),
                new RecipeDto("recipe-18", "Receta de Tocino", List.of(ingredientDtoList.get(19), ingredientDtoList.get(7)), List.of(categoria2), "Preparación de receta de tocino", 3),
                new RecipeDto("recipe-19", "Receta de Yogur", List.of(ingredientDtoList.get(20), ingredientDtoList.get(8)), List.of(categoria1), "Preparación de receta de yogur", 4),
                new RecipeDto("recipe-20", "Receta de Crema", List.of(ingredientDtoList.get(21), ingredientDtoList.get(9)), List.of(categoria3), "Preparación de receta de crema", 5),
                new RecipeDto("recipe-21", "Receta de Café", List.of(ingredientDtoList.get(22), ingredientDtoList.get(10)), List.of(categoria4), "Preparación de receta de café", 6),
                new RecipeDto("recipe-22", "Receta de Té", List.of(ingredientDtoList.get(23), ingredientDtoList.get(11)), List.of(categoria2), "Preparación de receta de té", 7),
                new RecipeDto("recipe-23", "Receta de Agua", List.of(ingredientDtoList.get(24), ingredientDtoList.get(12)), List.of(categoria1), "Preparación de receta de agua", 8),
                new RecipeDto("recipe-24", "Receta de Vino", List.of(ingredientDtoList.get(25), ingredientDtoList.get(13)), List.of(categoria3), "Preparación de receta de vino", 1),
                new RecipeDto("recipe-25", "Receta de Cerveza", List.of(ingredientDtoList.get(26), ingredientDtoList.get(14)), List.of(categoria4), "Preparación de receta de cerveza", 2),
                new RecipeDto("recipe-26", "Receta de Jugo", List.of(ingredientDtoList.get(27), ingredientDtoList.get(15)), List.of(categoria2), "Preparación de receta de jugo", 3),
                new RecipeDto("recipe-27", "Receta de Refresco", List.of(ingredientDtoList.get(28), ingredientDtoList.get(16)), List.of(categoria1), "Preparación de receta de refresco", 4),
                new RecipeDto("recipe-28", "Receta de Miel", List.of(ingredientDtoList.get(29), ingredientDtoList.get(17)), List.of(categoria3), "Preparación de receta de miel", 5),
                new RecipeDto("recipe-29", "Receta de Canela", List.of(ingredientDtoList.get(30), ingredientDtoList.get(18)), List.of(categoria4), "Preparación de receta de canela", 6),
                new RecipeDto("recipe-30", "Receta de Pimienta", List.of(ingredientDtoList.get(31), ingredientDtoList.get(19)), List.of(categoria2), "Preparación de receta de pimienta", 7),
                new RecipeDto("recipe-31", "Receta de Perejil", List.of(ingredientDtoList.get(32), ingredientDtoList.get(20)), List.of(categoria1), "Preparación de receta de perejil", 8),
                new RecipeDto("recipe-32", "Receta de Orégano", List.of(ingredientDtoList.get(33), ingredientDtoList.get(21)), List.of(categoria3), "Preparación de receta de orégano", 1),
                new RecipeDto("recipe-33", "Receta de Tomillo", List.of(ingredientDtoList.get(34), ingredientDtoList.get(22)), List.of(categoria4), "Preparación de receta de tomillo", 2),
                new RecipeDto("recipe-34", "Receta de Albahaca", List.of(ingredientDtoList.get(35), ingredientDtoList.get(23)), List.of(categoria2), "Preparación de receta de albahaca", 3),
                new RecipeDto("recipe-35", "Receta de Azúcar y Canela", List.of(ingredientDtoList.getFirst(), ingredientDtoList.get(30)), List.of(categoria4), "Preparación de receta de azúcar y canela", 4),
                new RecipeDto("recipe-36", "Receta de Miel y Yogur", List.of(ingredientDtoList.get(29), ingredientDtoList.get(20)), List.of(categoria1), "Preparación de receta de miel y yogur", 2)
        );
    }

    protected RecipeDto getExpectedRecipeDto(boolean desc, List<RecipeDto> otherRecipeDtoList) {
        List<RecipeDto> listToSort = (otherRecipeDtoList == null) ? recipeDtoList : otherRecipeDtoList;
        return getExpectedDto(desc, listToSort);
    }

    protected RecipeDto generateRecipeDto() {
        CategoryDto categoria1 = new CategoryDto("category-1", "Desayuno");
        return new RecipeDto("recipe-1", "Receta de Azúcar", List.of(ingredientDtoList.getFirst(), ingredientDtoList.get(10)), List.of(categoria1), "Preparación de receta de azúcar", 4);
    }

    public static List<RecipeDto> generateRecipeDtoListStatic() {
        return new BaseRecipesTest().recipeDtoList;
    }

    protected static RecipeDto generateRecipeDtoStatic() {
        return  new BaseRecipesTest().recipeDtoList.getFirst();
    }

    protected void validateResponse(String path, String mode, String queryParam, String queryParamValue, RecipeDto expectedFirstRecipe, List<RecipeDto> recipeDtoListFiltered) {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(path)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("mode", mode)
                        .queryParam(queryParam, queryParamValue)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(recipeDtoListFiltered.size())
                .jsonPath("$.content[0].id").isEqualTo(expectedFirstRecipe.getId())
                .jsonPath("$.content[0].name").isEqualTo(expectedFirstRecipe.getName())
                .jsonPath("$.pageable.pageNumber").isEqualTo(page)
                .jsonPath("$.pageable.pageSize").isEqualTo(size)
                .jsonPath("$.totalElements").isEqualTo(recipeDtoListFiltered.size())
                .jsonPath("$.totalPages").isEqualTo((int) Math.ceil((double) recipeDtoListFiltered.size() / size))
                .jsonPath("$.first").isEqualTo(true)
                .jsonPath("$.last").isEqualTo(true);
    }

}
