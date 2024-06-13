package com.whatacook.cookers.controller;

import com.whatacook.cookers.model.favorites.FavoriteDto;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.service.contracts.FavoriteDao;
import org.assertj.core.api.Assertions;
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
    private final String r = "recipe";
    private final String i = "ingredient";
    private final List<String> recipes = List.of("recipe1", "recipe2");
    private final List<String> ingredients = List.of("ingredient1", "ingredient2");

    @BeforeEach
    void setUp() {
        userDTO = userDtoBasicOk();
        favoriteDto = generateFavoriteDto();
        when(favoriteDao.findById(ID)).thenReturn(Mono.just(favoriteDto));
    }

    @Test
    void testGetFavoritesByID() {
        when(userDao.findByEmail(EMAIL)).thenReturn(Mono.just(userDTO));
        baseTestFavoritesOK(favoritesEndpoint, tokenUserOk(), requestFavorite(),
                USER_FAVORITES_RETRIEVED, favoriteDto);
    }

    @Test
    void testGetFavoritesByEmail() {
        when(userDao.findByEmail(EMAIL)).thenReturn(Mono.just(userDTO));
        baseTestFavoritesOK(favoritesEndpoint, tokenUserOk(), requestFavorite(),
                USER_FAVORITES_RETRIEVED, favoriteDto);
    }

    @Test
    void testFailGetFavoritesByID() {
        String invalidId = "invalid-id";
        userDTO.set_id(invalidId);
        when(userDao.findBy_id(anyString())).thenReturn(Mono.just(userDTO));
        baseTestFavoritesFail(favoritesEndpoint, tokenOtherUserOk(invalidId), requestFavorite(), UN_AUTH_MESSAGE);
    }


    @Test
    void testFailGetFavoritesByEmail() {
        String invalidEmail = "invalid@email.com";
        String invalidId = "invalid-id";
        userDTO.set_id(invalidId);
        userDTO.setEmail(invalidEmail);
        when(userDao.findByEmail(anyString())).thenReturn(Mono.just(userDTO));
        baseTestFavoritesFail(favoritesEndpoint, tokenOtherUserOk(invalidEmail), requestFavorite(), UN_AUTH_MESSAGE);
    }



    @SuppressWarnings("unchecked")
    void baseTestFavoritesOK(String path, String token, String body, String msg, FavoriteDto favoriteDto) {
        webTestClient.post().uri(path)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .consumeWith(response -> {
                    // Convert the response body to a string and print it
                    String receivedBody = new String(response.getResponseBody());
                    System.out.println(receivedBody);
                })
                .jsonPath("$.success").isEqualTo(true)
                .jsonPath("$.message").isEqualTo(msg)
                .jsonPath("$.content.id").isEqualTo(favoriteDto.getId())
                .jsonPath("$.content.recipes").value(recipesList -> {
                    assertThat(recipesList).isInstanceOf(List.class);
                    List<String> recipes = (List<String>) recipesList;
                    assertThat(recipes).containsAll(favoriteDto.getRecipes());
                })
                .jsonPath("$.content.ingredients").value(ingredientsList -> {
                    assertThat(ingredientsList).isInstanceOf(List.class);
                    List<String> ingredients = (List<String>) ingredientsList;
                    assertThat(ingredients).containsAll(favoriteDto.getIngredients());
                });
    }

    void baseTestFavoritesFail(String path, String token, String body, String message) {
        webTestClient.post().uri(path)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .consumeWith(response -> {
                    // Convert the response body to a string and print it
                    String receivedBody = new String(response.getResponseBody());
                    System.out.println(receivedBody);
                })
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message").value(text ->
                        Assertions.assertThat(text).asString().contains(message));

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

    private static String requestFavoriteRecipe(String userId, String recipeId) {
        return "{ \"userId\": \"" + userId + "\" " +
                "{ \"recipeId\": \"" + recipeId + "\" }";
    }

    private static String requestFavoriteIngredient(String userId, String ingredientId) {
        return "{ \"userId\": \"" + userId + "\" " +
                "{ \"ingredientId\": \"" + ingredientId + "\" }";
    }

}
