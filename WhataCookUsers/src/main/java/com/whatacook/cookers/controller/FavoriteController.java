package com.whatacook.cookers.controller;

import com.whatacook.cookers.config.jwt.AuthorizationUtil;
import com.whatacook.cookers.model.favorites.FavoriteRequest;
import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.service.FavoriteService;
import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping("${app.endpoint.favorites}")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping()
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SELF')")
    public Mono<Response> getFavorites(@RequestBody @Nonnull FavoriteRequest favoriteRequest) {
        return AuthorizationUtil.executeIfAuthorized(favoriteRequest,
                (json, userDetails) -> favoriteService.getFavorites(favoriteRequest));
    }

    @PostMapping("${app.endpoint.add-recipe}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SELF')")
    public Mono<Response> addFavoriteRecipe(@RequestBody @Nonnull FavoriteRequest favoriteRequest) {
        return AuthorizationUtil.executeIfAuthorized(favoriteRequest,
                (json, userDetails) -> favoriteService.addFavoriteRecipe(favoriteRequest));
    }

    @PostMapping("${app.endpoint.add-ingredient}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SELF')")
    public Mono<Response> addFavoriteIngredient(@RequestBody @Nonnull FavoriteRequest favoriteRequest) {
        return AuthorizationUtil.executeIfAuthorized(favoriteRequest,
                (json, userDetails) -> favoriteService.addFavoriteIngredient(favoriteRequest));
    }

    @PostMapping("${app.endpoint.remove-recipe}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SELF')")
    public Mono<Response> removeFavoriteRecipe(@RequestBody @Nonnull FavoriteRequest favoriteRequest) {
        return AuthorizationUtil.executeIfAuthorized(favoriteRequest,
                (json, userDetails) -> favoriteService.removeFavoriteRecipe(favoriteRequest));
    }

    @PostMapping("${app.endpoint.remove-ingredient}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('SELF')")
    public Mono<Response> removeFavoriteIngredient(@RequestBody @Nonnull FavoriteRequest favoriteRequest) {
        return AuthorizationUtil.executeIfAuthorized(favoriteRequest,
                (json, userDetails) -> favoriteService.removeFavoriteIngredient(favoriteRequest));
    }

}