package linkia.dam.whatacookrecipes.controller.ingredients;

import linkia.dam.whatacookrecipes.model.IngredientDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.Mockito.when;

public class GetIngredientsByNameContainingTest extends BaseIngredientsTest {

    public List<IngredientDto> ingredientDtoListFiltered;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        name = "c";
        size = 10;

        ingredientDtoListFiltered = ingredientDtoList.stream()
                .filter(ingredient -> ingredient.getName().contains(name))
                .collect(Collectors.toList());

        when(ingredientDao.findByNameContainingIgnoreCase(name)).thenReturn(Flux.fromIterable(ingredientDtoListFiltered));
    }

    private void validateResponse(String mode, String name, IngredientDto expectedFirstIngredient, int numberOfElements) {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(ingredientsUri + PATH_ByName)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .queryParam("mode", mode)
                        .queryParam("name", name)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(numberOfElements)
                .jsonPath("$.content[0].id").isEqualTo(expectedFirstIngredient.getId())
                .jsonPath("$.content[0].name").isEqualTo(expectedFirstIngredient.getName())
                .jsonPath("$.pageable.pageNumber").isEqualTo(page)
                .jsonPath("$.pageable.pageSize").isEqualTo(size)
                .jsonPath("$.totalElements").isEqualTo(ingredientDtoListFiltered.size())
                .jsonPath("$.totalPages").isEqualTo((int) Math.ceil((double) ingredientDtoListFiltered.size() / size))
                .jsonPath("$.first").isEqualTo(true)
                .jsonPath("$.last").isEqualTo(true);
    }

    @Test
    void getIngredientsByNameContainingAsc() {
        page = 0;
        IngredientDto expectedFirstIngredient = getExpectedIngredientDto(false, ingredientDtoListFiltered);

        validateResponse("", name, expectedFirstIngredient, ingredientDtoListFiltered.size());
    }

    @Test
    void getIngredientsByNameContainingDesc() {
        page = 0;
        IngredientDto expectedFirstIngredient = getExpectedIngredientDto(true, ingredientDtoListFiltered);

        validateResponse("D", name, expectedFirstIngredient, ingredientDtoListFiltered.size());
    }
}
