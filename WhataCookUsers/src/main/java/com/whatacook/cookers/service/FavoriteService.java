package com.whatacook.cookers.service;

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

    public static final String USER_FAVORITES_RETRIEVED = "User favorites retrieved";
    public static final String INGREDIENT_SUCCESSFULLY_REMOVED_FROM_FAVORITES = "Ingredient successfully removed from favorites";
    public static final String RECIPE_SUCCESSFULLY_REMOVED_FROM_FAVORITES = "Recipe successfully removed from favorites";
    public static final String INGREDIENT_SUCCESSFULLY_ADDED_TO_FAVORITES = "Ingredient successfully added to favorites";
    public static final String RECIPE_SUCCESSFULLY_ADDED_TO_FAVORITES = "Recipe successfully added to favorites";


    public Mono<Response> getFavorites(FavoriteRequest favoriteRequest) {
        return favoriteDao.findById(favoriteRequest.getUserId())
                .switchIfEmpty(createEmptyFavorite(favoriteRequest))
                .transform(this::ensureNonNullLists)
                .map(favorites -> success(USER_FAVORITES_RETRIEVED, favorites))
                .onErrorResume(e -> handleError("getFavorites", e));
    }

    public Mono<Response> addFavoriteRecipe(FavoriteRequest favoriteRequest) {
        return favoriteDao.findById(favoriteRequest.getUserId())
                .switchIfEmpty(createEmptyFavorite(favoriteRequest))
                .transform(this::ensureNonNullLists)
                .flatMap(favorites -> favorites.addRecipe(favoriteRequest.getRecipeId()))
                .flatMap(favoriteDao::save)
                .map(saved -> success(RECIPE_SUCCESSFULLY_ADDED_TO_FAVORITES, saved))
                .onErrorResume(e -> handleError("addFavoriteRecipe", e));
    }

    public Mono<Response> addFavoriteIngredient(FavoriteRequest favoriteRequest) {
        return favoriteDao.findById(favoriteRequest.getUserId())
                .switchIfEmpty(createEmptyFavorite(favoriteRequest))
                .transform(this::ensureNonNullLists)
                .flatMap(favorites -> favorites.addIngredient(favoriteRequest.getIngredientId()))
                .flatMap(favoriteDao::save)
                .map(saved -> success(INGREDIENT_SUCCESSFULLY_ADDED_TO_FAVORITES, saved))
                .onErrorResume(e -> handleError("addFavoriteIngredient", e));
    }

    public Mono<Response> removeFavoriteRecipe(FavoriteRequest favoriteRequest) {
        return favoriteDao.findById(favoriteRequest.getUserId())
                .switchIfEmpty(createEmptyFavorite(favoriteRequest))
                .transform(this::ensureNonNullLists)
                .flatMap(favorites -> favorites.removeRecipe(favoriteRequest.getRecipeId()))
                .flatMap(favoriteDao::save)
                .map(saved -> success(RECIPE_SUCCESSFULLY_REMOVED_FROM_FAVORITES, saved))
                .onErrorResume(e -> handleError("removeFavoriteRecipe", e));
    }

    public Mono<Response> removeFavoriteIngredient(FavoriteRequest favoriteRequest) {
        return favoriteDao.findById(favoriteRequest.getUserId())
                .switchIfEmpty(createEmptyFavorite(favoriteRequest))
                .transform(this::ensureNonNullLists)
                .flatMap(favorites -> favorites.removeIngredient(favoriteRequest.getIngredientId()))
                .flatMap(favoriteDao::save)
                .map(saved -> success(INGREDIENT_SUCCESSFULLY_REMOVED_FROM_FAVORITES, saved))
                .onErrorResume(e -> handleError("removeFavoriteIngredient", e));
    }

    private Mono<FavoriteDto> createEmptyFavorite(FavoriteRequest favoriteRequest) {
        return Mono.defer(() -> Mono.just(new FavoriteDto(favoriteRequest.getUserId(), new ArrayList<>(), new ArrayList<>())));
    }

    private Mono<FavoriteDto> ensureNonNullLists(Mono<FavoriteDto> favoriteDtoMono) {
        return favoriteDtoMono.map(favorites -> {
            favorites.setRecipes(Optional.ofNullable(favorites.getRecipes()).orElseGet(ArrayList::new));
            favorites.setIngredients(Optional.ofNullable(favorites.getIngredients()).orElseGet(ArrayList::new));
            return favorites;
        });
    }

    private Mono<Response> handleError(String methodName, Throwable e) {
        log.error("Error in {}: {}", methodName, e.getMessage(), e);
        return Response.monoError(e);
    }

}
