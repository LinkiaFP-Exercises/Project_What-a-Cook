package com.whatacook.cookers.controller;

import com.whatacook.cookers.model.favorites.FavoriteDto;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.service.contracts.FavoriteDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.whatacook.cookers.service.FavoriteService.USER_FAVORITES_RETRIEVED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class FavoriteControllerTest extends BaseTestClass {

    @MockBean
    private FavoriteDao favoriteDao;

    @Value("${app.endpoint.favorites}")
    private String favoritesEndpoint;
    @Value("${app.endpoint.add-recipe}")
    private String addRecipeEndpoint;
    @Value("${app.endpoint.add-ingredient}")
    private String addIngredientEndpoint;
    @Value("${app.endpoint.remove-recipe}")
    private String removeRecipeEndpoint;
    @Value("${app.endpoint.remove-ingredient}")
    private String removeIngredientEndpoint;

    private FavoriteDto favoriteDto;
    private UserDTO userDTO;
    private String token;
    private List<String> recipes = List.of("recipe1", "recipe2");
    private List<String> ingredients = List.of("ingredient1", "ingredient2");

    @BeforeEach
    void setUp() {
        userDTO = userDtoBasicOk();
        favoriteDto = generateFavoriteDto();
        when(userDao.findBy_id(ID)).thenReturn(Mono.just(userDTO));
        when(favoriteDao.findById(anyString())).thenReturn(Mono.just(favoriteDto));
    }

    @Test
    void testGetFavorites() {
        webTestClient.post().uri(favoritesEndpoint)
                .header("Authorization", tokenUserOk(userDTO.get_id()))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestFavorite())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(response -> {
                    // Convert the response body to a string and print it
                    String body = new String(response.getResponseBody());
                    System.out.println();
                    System.out.println(body);
                    System.out.println();
                })
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo(USER_FAVORITES_RETRIEVED)
                .jsonPath("$.content.id").isEqualTo(userDTO.get_id())
                .jsonPath("$.content.recipes").value(recipesList -> {
                    assertThat(recipesList).isInstanceOf(List.class);
                    List<String> recipes = (List<String>) recipesList;
                    assertThat(recipes).containsAll(this.recipes);
                })
                .jsonPath("$.content.ingredients").value(ingredientsList -> {
                    assertThat(ingredientsList).isInstanceOf(List.class);
                    List<String> ingredients = (List<String>) ingredientsList;
                    assertThat(ingredients).containsAll(this.ingredients);
                });
    }


    private FavoriteDto generateFavoriteDto() {
        FavoriteDto favoriteDto = new FavoriteDto();
        favoriteDto.setId(ID);
        favoriteDto.setRecipes(recipes);
        favoriteDto.setIngredients(ingredients);
        return favoriteDto;
    }

    private static String requestFavorite() {
        return "{ \"userId\": \"" + ID + "\" }";
    }

    void baseTestFavorites_Ok(String path, String token, String body, boolean success, String msg) {
        webTestClient.post().uri(path)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.success").isEqualTo(success)
                .jsonPath("$.message").isEqualTo(msg)
                .jsonPath("$.content.userId").isEqualTo(userDTO.get_id())
                .jsonPath("$.content.recipes").value(recipesList -> {
                    assertThat(recipesList).isInstanceOf(List.class);
                    List<String> recipes = (List<String>) recipesList;
                    assertThat(recipes).containsAll(this.recipes);
                })
                .jsonPath("$.content.ingredients").value(ingredientsList -> {
                    assertThat(ingredientsList).isInstanceOf(List.class);
                    List<String> ingredients = (List<String>) ingredientsList;
                    assertThat(ingredients).containsAll(this.ingredients);
                });
    }



    private static String requestFavoriteRecipe(String userId, String recipeId) {
        return "{ \"userId\": \"" + userId + "\" " +
                "{ \"recipeId\": \"" + recipeId + "\" }";
    }

    private static String requestFavoriteIngredient(String userId, String ingredientId) {
        return "{ \"userId\": \"" + userId + "\" " +
                "{ \"ingredientId\": \"" + ingredientId + "\" }";
    }


}
