package linkia.dam.whatacookrecipes.controller.recipes;

import linkia.dam.whatacookrecipes.model.RecipeDto;
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

public class GetRecipesByIdsTest extends BaseRecipesTest {



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pathVariable = recipesUri + byIdsEndpoint;
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @Test
    void testGetRecipesByIds() {
        RecipeDto recipe1 = recipeDtoList.getFirst();
        RecipeDto recipe2 = recipeDtoList.getLast();
        String idNotFounded = "recipe-40";

        List<String> ids = Arrays.asList(recipe1.getId(), recipe2.getId(), idNotFounded);
        List<RecipeDto> foundRecipes = Arrays.asList(recipe1, recipe2);
        List<String> notFoundIds = Arrays.asList(idNotFounded);

        Map<String, Object> response = new HashMap<>();
        response.put("found", foundRecipes);
        response.put("notFound", notFoundIds);

        when(recipeDao.findAllById(anyList())).thenReturn(Flux.fromIterable(foundRecipes));

        webTestClient.post()
                .uri(pathVariable)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ids)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.found").isArray()
                .jsonPath("$.found.length()").isEqualTo(foundRecipes.size())
                .jsonPath("$.found[0].id").isEqualTo(recipe1.getId())
                .jsonPath("$.found[0].name").isEqualTo(recipe1.getName())
                .jsonPath("$.notFound").isArray()
                .jsonPath("$.notFound.length()").isEqualTo(notFoundIds.size())
                .jsonPath("$.notFound[0]").isEqualTo(notFoundIds.getFirst());
    }
}
