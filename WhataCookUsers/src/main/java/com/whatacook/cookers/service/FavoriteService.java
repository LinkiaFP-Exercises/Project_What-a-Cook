package com.whatacook.cookers.service;

import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.favorites.FavoriteRequest;
import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.service.contracts.FavoriteDao;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.whatacook.cookers.model.responses.Response.success;

@Slf4j
@AllArgsConstructor
@Service
public class FavoriteService {

    public static final String USER_FAVORITES_NOT_FOUND = "User favorites not found!";
    private final FavoriteDao favoriteDao;

    public Mono<Response> addFavoriteRecipe(FavoriteRequest favoriteRequest) {
        return favoriteDao.findById(favoriteRequest.getUserId())
                .flatMap(favorites -> favorites.addRecipe(favoriteRequest.getRecipeId()))
                .flatMap(favoriteDao::save)
                .map(saved -> success("Recipe successfully added to favorites", saved))
                .switchIfEmpty(UserServiceException.mono(USER_FAVORITES_NOT_FOUND))
                .onErrorResume(Exception.class, Response::monoError);
    }

    public Mono<Response> addFavoriteIngredient(FavoriteRequest favoriteRequest) {
        return favoriteDao.findById(favoriteRequest.getUserId())
                .flatMap(favorites -> favorites.addIngredient(favoriteRequest.getIngredientId()))
                .flatMap(favoriteDao::save)
                .map(saved -> success("Ingredient successfully added to favorites", saved))
                .switchIfEmpty(UserServiceException.mono(USER_FAVORITES_NOT_FOUND))
                .onErrorResume(Exception.class, Response::monoError);
    }

    public Mono<Response> removeFavoriteRecipe(FavoriteRequest favoriteRequest) {
        return favoriteDao.findById(favoriteRequest.getUserId())
                .flatMap(favorites -> favorites.removeRecipe(favoriteRequest.getRecipeId()))
                .flatMap(favoriteDao::save)
                .map(saved -> success("Recipe successfully removed from favorites", saved))
                .switchIfEmpty(UserServiceException.mono(USER_FAVORITES_NOT_FOUND))
                .onErrorResume(Exception.class, Response::monoError);
    }

    public Mono<Response> removeFavoriteIngredient(FavoriteRequest favoriteRequest) {
        return favoriteDao.findById(favoriteRequest.getUserId())
                .flatMap(favorites -> favorites.removeIngredient(favoriteRequest.getIngredientId()))
                .flatMap(favoriteDao::save)
                .map(saved -> success("Ingredient successfully removed from favorites", saved))
                .switchIfEmpty(UserServiceException.mono(USER_FAVORITES_NOT_FOUND))
                .onErrorResume(Exception.class, Response::monoError);
    }

    public Mono<Response> getFavorites(FavoriteRequest favoriteRequest) {
        return favoriteDao.findById(favoriteRequest.getUserId())
                .map(favorites -> success("User favorites retrieved", favorites))
                .switchIfEmpty(UserServiceException.mono(USER_FAVORITES_NOT_FOUND))
                .onErrorResume(Exception.class, Response::monoError);
    }
}
