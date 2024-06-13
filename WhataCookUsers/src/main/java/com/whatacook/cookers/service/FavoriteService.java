package com.whatacook.cookers.service;

import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.favorites.FavoriteDto;
import com.whatacook.cookers.model.favorites.FavoriteRequest;
import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.service.contracts.FavoriteDao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Optional;

import static com.whatacook.cookers.model.responses.Response.success;

@Slf4j
@AllArgsConstructor
@Service
public class FavoriteService {

    private final FavoriteDao favoriteDao;

    public static final String USER_FAVORITES_NOT_FOUND = "User favorites not found!";
    public static final String USER_FAVORITES_RETRIEVED = "User favorites retrieved";
    public static final String INGREDIENT_SUCCESSFULLY_REMOVED_FROM_FAVORITES = "Ingredient successfully removed from favorites";
    public static final String RECIPE_SUCCESSFULLY_REMOVED_FROM_FAVORITES = "Recipe successfully removed from favorites";
    public static final String INGREDIENT_SUCCESSFULLY_ADDED_TO_FAVORITES = "Ingredient successfully added to favorites";
    public static final String RECIPE_SUCCESSFULLY_ADDED_TO_FAVORITES = "Recipe successfully added to favorites";


    public Mono<Response> addFavoriteRecipe(FavoriteRequest favoriteRequest) {
        return favoriteDao.findById(favoriteRequest.getUserId())
                .transform(this::ensureRecipesNonNullLists)
                .flatMap(favorites -> favorites.addRecipe(favoriteRequest.getRecipeId()))
                .flatMap(favoriteDao::save)
                .map(saved -> success(RECIPE_SUCCESSFULLY_ADDED_TO_FAVORITES, saved))
                    .switchIfEmpty(UserServiceException.mono(USER_FAVORITES_NOT_FOUND))
                .onErrorResume(Exception.class, e -> {
                    log.error("Error adding favorite recipe", e);
                    return Response.monoError(e);
                });
    }

    public Mono<Response> addFavoriteIngredient(FavoriteRequest favoriteRequest) {
        return favoriteDao.findById(favoriteRequest.getUserId())
                .transform(this::ensureIngredientsNonNullLists)
                .flatMap(favorites -> favorites.addIngredient(favoriteRequest.getIngredientId()))
                .flatMap(favoriteDao::save)
                .map(saved -> success(INGREDIENT_SUCCESSFULLY_ADDED_TO_FAVORITES, saved))
                .switchIfEmpty(UserServiceException.mono(USER_FAVORITES_NOT_FOUND))
                .onErrorResume(Exception.class, Response::monoError);
    }

    public Mono<Response> removeFavoriteRecipe(FavoriteRequest favoriteRequest) {
        return favoriteDao.findById(favoriteRequest.getUserId())
                .transform(this::ensureRecipesNonNullLists)
                .flatMap(favorites -> favorites.removeRecipe(favoriteRequest.getRecipeId()))
                .flatMap(favoriteDao::save)
                .map(saved -> success(RECIPE_SUCCESSFULLY_REMOVED_FROM_FAVORITES, saved))
                .switchIfEmpty(UserServiceException.mono(USER_FAVORITES_NOT_FOUND))
                .onErrorResume(Exception.class, Response::monoError);
    }

    public Mono<Response> removeFavoriteIngredient(FavoriteRequest favoriteRequest) {
        return favoriteDao.findById(favoriteRequest.getUserId())
                .transform(this::ensureIngredientsNonNullLists)
                .flatMap(favorites -> favorites.removeIngredient(favoriteRequest.getIngredientId()))
                .flatMap(favoriteDao::save)
                .map(saved -> success(INGREDIENT_SUCCESSFULLY_REMOVED_FROM_FAVORITES, saved))
                .switchIfEmpty(UserServiceException.mono(USER_FAVORITES_NOT_FOUND))
                .onErrorResume(Exception.class, Response::monoError);
    }

    public Mono<Response> getFavorites(FavoriteRequest favoriteRequest) {
        return favoriteDao.findById(favoriteRequest.getUserId())
                .transform(this::ensureNonNullLists)
                .map(favorites -> success(USER_FAVORITES_RETRIEVED, favorites))
                .switchIfEmpty(UserServiceException.mono(USER_FAVORITES_NOT_FOUND))
                .onErrorResume(Exception.class, Response::monoError);
    }

    private Mono<FavoriteDto> ensureNonNullLists(Mono<FavoriteDto> favoriteDtoMono) {
        return favoriteDtoMono.map(favorites -> {
            favorites.setRecipes(Optional.ofNullable(favorites.getRecipes()).orElseGet(ArrayList::new));
            favorites.setIngredients(Optional.ofNullable(favorites.getIngredients()).orElseGet(ArrayList::new));
            return favorites;
        });
    }

    private Mono<FavoriteDto> ensureRecipesNonNullLists(Mono<FavoriteDto> favoriteDtoMono) {
        return favoriteDtoMono.map(favorites -> {
            favorites.setRecipes(Optional.ofNullable(favorites.getRecipes()).orElseGet(ArrayList::new));
            return favorites;
        });
    }

    private Mono<FavoriteDto> ensureIngredientsNonNullLists(Mono<FavoriteDto> favoriteDtoMono) {
        return favoriteDtoMono.map(favorites -> {
            favorites.setIngredients(Optional.ofNullable(favorites.getIngredients()).orElseGet(ArrayList::new));
            return favorites;
        });
    }
}
