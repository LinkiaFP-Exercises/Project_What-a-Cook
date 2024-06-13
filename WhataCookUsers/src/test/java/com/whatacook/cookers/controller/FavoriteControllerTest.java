package com.whatacook.cookers.controller;

import com.whatacook.cookers.model.favorites.FavoriteDto;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.service.contracts.FavoriteDao;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.whatacook.cookers.service.FavoriteService.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
    private FavoriteDto favoriteDtoVerify;
    private UserDTO userDTO;
    private final String recipe = "recipe-X";
    private final String ingredient = "ingredient-x";
    private final List<String> recipes = new ArrayList<>(List.of("recipe1", "recipe2"));
    private final List<String> ingredients = new ArrayList<>(List.of("ingredient1", "ingredient2"));

    @BeforeEach
    void setUp() {
        userDTO = userDtoBasicOk();
        favoriteDto = generateFavoriteDto();
        favoriteDtoVerify = generateFavoriteDto();
        // Crear un spy para favoriteDto
        favoriteDto = Mockito.spy(favoriteDto);
        when(userDao.findByEmail(EMAIL)).thenReturn(Mono.just(userDTO));
        when(favoriteDao.findById(ID)).thenReturn(Mono.just(favoriteDto));
        when(favoriteDao.save(any(FavoriteDto.class)))
                .thenAnswer(invocation -> Mono.just(invocation.getArguments()[0]));
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
    void testAddFavoriteRecipeByID() {
        favoriteDtoVerify.getRecipes().add(recipe);
        pathVariable = favoritesEndpoint + addRecipeEndpoint;
        baseTestFavoritesOK(pathVariable, tokenUserOk(), requestFavoriteRecipe(),
                RECIPE_SUCCESSFULLY_ADDED_TO_FAVORITES, favoriteDtoVerify);

        // Verificar que el método addRecipe fue llamado
        Mockito.verify(favoriteDto).addRecipe(recipe);
    }

    @Test
    void testAddFavoriteRecipeEmail() {
        favoriteDtoVerify.getRecipes().add(recipe);
        pathVariable = favoritesEndpoint + addRecipeEndpoint;
        baseTestFavoritesOK(pathVariable, tokenUserOk(), requestFavoriteRecipe(),
                RECIPE_SUCCESSFULLY_ADDED_TO_FAVORITES, favoriteDtoVerify);

        // Verificar que el método addRecipe fue llamado
        Mockito.verify(favoriteDto).addRecipe(recipe);
    }

    @Test
    void testRemoveFavoriteRecipeByID() {
        favoriteDto.getRecipes().add(recipe);
        pathVariable = favoritesEndpoint + removeRecipeEndpoint;
        baseTestFavoritesOK(pathVariable, tokenUserOk(), requestFavoriteRecipe(),
                RECIPE_SUCCESSFULLY_REMOVED_FROM_FAVORITES, favoriteDtoVerify);

        // Verificar que el método removeRecipe fue llamado
        Mockito.verify(favoriteDto).removeRecipe(recipe);
    }

    @Test
    void testRemoveFavoriteRecipeEmail() {
        favoriteDto.getRecipes().add(recipe);
        pathVariable = favoritesEndpoint + removeRecipeEndpoint;
        baseTestFavoritesOK(pathVariable, tokenUserOk(), requestFavoriteRecipe(),
                RECIPE_SUCCESSFULLY_REMOVED_FROM_FAVORITES, favoriteDtoVerify);

        // Verificar que el método removeRecipe fue llamado
        Mockito.verify(favoriteDto).removeRecipe(recipe);
    }

    @Test
    void testAddFavoriteIngredientByID() {
        favoriteDtoVerify.getIngredients().add(ingredient);
        pathVariable = favoritesEndpoint + addIngredientEndpoint;
        baseTestFavoritesOK(pathVariable, tokenUserOk(), requestFavoriteIngredient(),
                INGREDIENT_SUCCESSFULLY_ADDED_TO_FAVORITES, favoriteDtoVerify);

        // Verificar que el método addIngredient fue llamado
        Mockito.verify(favoriteDto).addIngredient(ingredient);
    }

    @Test
    void testAddFavoriteIngredientEmail() {
        favoriteDtoVerify.getIngredients().add(ingredient);
        pathVariable = favoritesEndpoint + addIngredientEndpoint;
        baseTestFavoritesOK(pathVariable, tokenUserOk(), requestFavoriteIngredient(),
                INGREDIENT_SUCCESSFULLY_ADDED_TO_FAVORITES, favoriteDtoVerify);

        // Verificar que el método addIngredient fue llamado
        Mockito.verify(favoriteDto).addIngredient(ingredient);
    }

    @Test
    void testRemoveFavoriteIngredientByID() {
        favoriteDto.getIngredients().add(ingredient);
        pathVariable = favoritesEndpoint + removeIngredientEndpoint;
        baseTestFavoritesOK(pathVariable, tokenUserOk(), requestFavoriteIngredient(),
                INGREDIENT_SUCCESSFULLY_REMOVED_FROM_FAVORITES, favoriteDtoVerify);

        // Verificar que el método removeIngredient fue llamado
        Mockito.verify(favoriteDto).removeIngredient(ingredient);
    }

    @Test
    void testRemoveFavoriteIngredientEmail() {
        favoriteDto.getIngredients().add(ingredient);
        pathVariable = favoritesEndpoint + removeIngredientEndpoint;
        baseTestFavoritesOK(pathVariable, tokenUserOk(), requestFavoriteIngredient(),
                INGREDIENT_SUCCESSFULLY_REMOVED_FROM_FAVORITES, favoriteDtoVerify);

        // Verificar que el método removeIngredient fue llamado
        Mockito.verify(favoriteDto).removeIngredient(ingredient);
    }


    @Test
    void testFailGetFavoritesByID() {
        String invalidId = "invalid-id";
        userDTO.set_id(invalidId);
        when(userDao.findBy_id(anyString())).thenReturn(Mono.just(userDTO));
        baseTestFavoritesFail(favoritesEndpoint, tokenOtherUserOk(invalidId), requestFavorite());
    }


    @Test
    void testFailGetFavoritesByEmail() {
        String invalidEmail = "invalid@email.com";
        String invalidId = "invalid-id";
        userDTO.set_id(invalidId);
        userDTO.setEmail(invalidEmail);
        when(userDao.findByEmail(anyString())).thenReturn(Mono.just(userDTO));
        baseTestFavoritesFail(favoritesEndpoint, tokenOtherUserOk(invalidEmail), requestFavorite());
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
                    String receivedBody = new String(Objects.requireNonNull(response.getResponseBody()));
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

    void baseTestFavoritesFail(String path, String token, String body) {
        webTestClient.post().uri(path)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .consumeWith(response -> {
                    // Convert the response body to a string and print it
                    String receivedBody = new String(Objects.requireNonNull(response.getResponseBody()));
                    System.out.println(receivedBody);
                })
                .jsonPath("$.success").isEqualTo(false)
                .jsonPath("$.message").value(text ->
                        Assertions.assertThat(text).asString().contains(BaseTestClass.UN_AUTH_MESSAGE));

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

    private static String requestFavoriteRecipe() {
        return "{ \"userId\": \"" + ID + "\", " +
                "\"recipeId\": \"" + "recipe-X" + "\" }";
    }

    private static String requestFavoriteIngredient() {
        return "{ \"userId\": \"" + ID + "\", " +
                "\"ingredientId\": \"" + "ingredient-x" + "\" }";
    }


}
