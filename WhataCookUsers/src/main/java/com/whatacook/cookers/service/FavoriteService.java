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

/**
 * Service class for handling favorite-related operations.
 * <p>
 * Annotations:
 * - @Slf4j: Enables logging.
 * - @AllArgsConstructor: Generates a constructor with 1 parameter for each field.
 * - @Service: Indicates that this class is a Spring service.
 * <p>
 * Fields:
 * - favoriteDao: The favorite data access object for interacting with the database.
 * <p>
 * Methods:
 * - getFavorites(FavoriteRequest favoriteRequest): Retrieves the favorites of a user.
 * - addFavoriteRecipe(FavoriteRequest favoriteRequest): Adds a recipe to the user's favorites.
 * - addFavoriteIngredient(FavoriteRequest favoriteRequest): Adds an ingredient to the user's favorites.
 * - removeFavoriteRecipe(FavoriteRequest favoriteRequest): Removes a recipe from the user's favorites.
 * - removeFavoriteIngredient(FavoriteRequest favoriteRequest): Removes an ingredient from the user's favorites.
 * - createEmptyFavorite(FavoriteRequest favoriteRequest): Creates an empty favorite entry for a user.
 * - ensureNonNullLists(Mono<FavoriteDto> favoriteDtoMono): Ensures that the favorite lists are not null.
 * - handleError(String methodName, Throwable e): Handles errors that occur during processing.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see FavoriteDao
 * @see FavoriteDto
 * @see FavoriteRequest
 * @see Response
 * @see Mono
 * @see Service
 * @see AllArgsConstructor
 * @see Slf4j
 */
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

    /**
     * Retrieves the favorites of a user.
     *
     * @param favoriteRequest the favorite request containing the user ID
     * @return a Mono containing the response with the user's favorites
     */
    public Mono<Response> getFavorites(FavoriteRequest favoriteRequest) {
        return favoriteDao.findById(favoriteRequest.getUserId())
                .switchIfEmpty(createEmptyFavorite(favoriteRequest))
                .transform(this::ensureNonNullLists)
                .map(favorites -> success(USER_FAVORITES_RETRIEVED, favorites))
                .onErrorResume(e -> handleError("getFavorites", e));
    }

    /**
     * Adds a recipe to the user's favorites.
     *
     * @param favoriteRequest the favorite request containing the user ID and recipe ID
     * @return a Mono containing the response with the updated favorites
     */
    public Mono<Response> addFavoriteRecipe(FavoriteRequest favoriteRequest) {
        return favoriteDao.findById(favoriteRequest.getUserId())
                .switchIfEmpty(createEmptyFavorite(favoriteRequest))
                .transform(this::ensureNonNullLists)
                .flatMap(favorites -> favorites.addRecipe(favoriteRequest.getRecipeId()))
                .flatMap(favoriteDao::save)
                .map(saved -> success(RECIPE_SUCCESSFULLY_ADDED_TO_FAVORITES, saved))
                .onErrorResume(e -> handleError("addFavoriteRecipe", e));
    }

    /**
     * Adds an ingredient to the user's favorites.
     *
     * @param favoriteRequest the favorite request containing the user ID and ingredient ID
     * @return a Mono containing the response with the updated favorites
     */
    public Mono<Response> addFavoriteIngredient(FavoriteRequest favoriteRequest) {
        return favoriteDao.findById(favoriteRequest.getUserId())
                .switchIfEmpty(createEmptyFavorite(favoriteRequest))
                .transform(this::ensureNonNullLists)
                .flatMap(favorites -> favorites.addIngredient(favoriteRequest.getIngredientId()))
                .flatMap(favoriteDao::save)
                .map(saved -> success(INGREDIENT_SUCCESSFULLY_ADDED_TO_FAVORITES, saved))
                .onErrorResume(e -> handleError("addFavoriteIngredient", e));
    }

    /**
     * Removes a recipe from the user's favorites.
     *
     * @param favoriteRequest the favorite request containing the user ID and recipe ID
     * @return a Mono containing the response with the updated favorites
     */
    public Mono<Response> removeFavoriteRecipe(FavoriteRequest favoriteRequest) {
        return favoriteDao.findById(favoriteRequest.getUserId())
                .switchIfEmpty(createEmptyFavorite(favoriteRequest))
                .transform(this::ensureNonNullLists)
                .flatMap(favorites -> favorites.removeRecipe(favoriteRequest.getRecipeId()))
                .flatMap(favoriteDao::save)
                .map(saved -> success(RECIPE_SUCCESSFULLY_REMOVED_FROM_FAVORITES, saved))
                .onErrorResume(e -> handleError("removeFavoriteRecipe", e));
    }

    /**
     * Removes an ingredient from the user's favorites.
     *
     * @param favoriteRequest the favorite request containing the user ID and ingredient ID
     * @return a Mono containing the response with the updated favorites
     */
    public Mono<Response> removeFavoriteIngredient(FavoriteRequest favoriteRequest) {
        return favoriteDao.findById(favoriteRequest.getUserId())
                .switchIfEmpty(createEmptyFavorite(favoriteRequest))
                .transform(this::ensureNonNullLists)
                .flatMap(favorites -> favorites.removeIngredient(favoriteRequest.getIngredientId()))
                .flatMap(favoriteDao::save)
                .map(saved -> success(INGREDIENT_SUCCESSFULLY_REMOVED_FROM_FAVORITES, saved))
                .onErrorResume(e -> handleError("removeFavoriteIngredient", e));
    }

    /**
     * Creates an empty favorite entry for a user.
     *
     * @param favoriteRequest the favorite request containing the user ID
     * @return a Mono containing the created empty favorite entry
     */
    private Mono<FavoriteDto> createEmptyFavorite(FavoriteRequest favoriteRequest) {
        return Mono.defer(() -> Mono.just(new FavoriteDto(favoriteRequest.getUserId(), new ArrayList<>(), new ArrayList<>())));
    }

    /**
     * Ensures that the favorite lists are not null.
     *
     * @param favoriteDtoMono the Mono containing the favorite DTO
     * @return a Mono containing the favorite DTO with non-null lists
     */
    private Mono<FavoriteDto> ensureNonNullLists(Mono<FavoriteDto> favoriteDtoMono) {
        return favoriteDtoMono.map(favorites -> {
            favorites.setRecipes(Optional.ofNullable(favorites.getRecipes()).orElseGet(ArrayList::new));
            favorites.setIngredients(Optional.ofNullable(favorites.getIngredients()).orElseGet(ArrayList::new));
            return favorites;
        });
    }

    /**
     * Handles errors that occur during processing.
     *
     * @param methodName the name of the method where the error occurred
     * @param e          the throwable representing the error
     * @return a Mono containing the response with the error message
     */
    private Mono<Response> handleError(String methodName, Throwable e) {
        log.error("Error in {}: {}", methodName, e.getMessage(), e);
        return Response.monoError(e);
    }

}
