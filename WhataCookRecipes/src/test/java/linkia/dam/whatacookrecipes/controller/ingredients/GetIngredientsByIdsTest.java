package linkia.dam.whatacookrecipes.controller.ingredients;

import linkia.dam.whatacookrecipes.model.IngredientDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

public class GetIngredientsByIdsTest extends BaseIngredientsTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pathVariable = ingredientsUri + byIdsEndpoint;
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Test
    void testGetIngredientsByIds() {
        IngredientDto ingredient1 = ingredientDtoList.getFirst();
        IngredientDto ingredient2 = ingredientDtoList.getLast();
        String idNotFounded = "ingredient-3";

        List<String> ids = Arrays.asList(ingredient1.getId(), ingredient2.getId(), idNotFounded);
        List<IngredientDto> foundIngredients = Arrays.asList(ingredient1, ingredient2);
        List<String> notFoundIds = List.of(idNotFounded);

        Map<String, Object> response = new HashMap<>();
        response.put("found", foundIngredients);
        response.put("notFound", notFoundIds);

        when(ingredientDao.findAllById(anyList())).thenReturn(Flux.fromIterable(foundIngredients));

        webTestClient.post()
                .uri(pathVariable)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ids)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.found").isArray()
                .jsonPath("$.found.length()").isEqualTo(foundIngredients.size())
                .jsonPath("$.found[0].id").isEqualTo(ingredient1.getId())
                .jsonPath("$.found[0].name").isEqualTo(ingredient1.getName())
                .jsonPath("$.notFound").isArray()
                .jsonPath("$.notFound.length()").isEqualTo(notFoundIds.size())
                .jsonPath("$.notFound[0]").isEqualTo(notFoundIds.getFirst());
    }
}
