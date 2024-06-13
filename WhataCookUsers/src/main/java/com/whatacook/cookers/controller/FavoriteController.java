package com.whatacook.cookers.controller;

import com.whatacook.cookers.config.jwt.AuthorizationUtil;
import com.whatacook.cookers.model.favorites.FavoriteRequest;
import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.service.FavoriteService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.function.BiFunction;

@AllArgsConstructor
@RestController
@RequestMapping("${app.endpoint.favorites}")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping()
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SELF')")
    public Mono<Response> getFavorites(@RequestBody FavoriteRequest favoriteRequest) {
        return processFavoriteRequest(favoriteRequest, favoriteService::getFavorites);
    }

    @PostMapping("/add-recipe")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SELF')")
    public Mono<Response> addFavoriteRecipe(@RequestBody FavoriteRequest favoriteRequest) {
        return processFavoriteRequest(favoriteRequest, favoriteService::addFavoriteRecipe);
    }

    @PostMapping("/add-ingredient")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SELF')")
    public Mono<Response> addFavoriteIngredient(@RequestBody FavoriteRequest favoriteRequest) {
        return processFavoriteRequest(favoriteRequest, favoriteService::addFavoriteIngredient);
    }

    @PostMapping("/remove-recipe")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SELF')")
    public Mono<Response> removeFavoriteRecipe(@RequestBody FavoriteRequest favoriteRequest) {
        return processFavoriteRequest(favoriteRequest, favoriteService::removeFavoriteRecipe);
    }

    @PostMapping("/remove-ingredient")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SELF')")
    public Mono<Response> removeFavoriteIngredient(@RequestBody FavoriteRequest favoriteRequest) {
        return processFavoriteRequest(favoriteRequest, favoriteService::removeFavoriteIngredient);
    }

    private Mono<Response> processFavoriteRequest(FavoriteRequest favoriteRequest, BiFunction<UserJson, FavoriteRequest, Mono<Response>> action) {
        return AuthorizationUtil.executeIfAuthorized(new UserJson(favoriteRequest.getUserId()),
                (json, userDetails) -> action.apply(json, favoriteRequest));
    }
}
