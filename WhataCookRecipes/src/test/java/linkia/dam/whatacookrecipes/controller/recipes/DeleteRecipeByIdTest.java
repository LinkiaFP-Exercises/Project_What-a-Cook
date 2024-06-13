package linkia.dam.whatacookrecipes.controller.recipes;

import linkia.dam.whatacookrecipes.model.RecipeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class DeleteRecipeByIdTest extends BaseRecipesTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        recipeDto = generateRecipeDto();
        pathVariable = recipesUri + PATH_VARIABLE_ID;
        valuePathVariable = recipeDto.getId();
    }

    @Test
    void testDeleteRecipeByIdFounded() {
        when(recipeDao.findById(anyString())).thenReturn(Mono.just(recipeDto));
        when(recipeDao.delete(recipeDto)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder.path(pathVariable)
                        .build(valuePathVariable))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> {
                    assert response.contains(recipeDto.getName());
                    assert response.contains(DELETED);
                });
    }

    @Test
    void testDeleteRecipeByIdNotFound() {
        when(recipeDao.findById(anyString())).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder.path(pathVariable)
                        .build(valuePathVariable))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

        verify(recipeDao, times(0)).delete(any(RecipeDto.class));
    }
}
