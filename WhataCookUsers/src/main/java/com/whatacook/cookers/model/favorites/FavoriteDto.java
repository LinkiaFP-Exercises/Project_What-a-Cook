package com.whatacook.cookers.model.favorites;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Mono;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Document(collection = "favorite")
public class FavoriteDto {

    @Id
    private String id;
    private List<String> favoriteRecipes;
    private List<String> favoriteIngredients;

    // Métodos para favoriteRecipes
    public Mono<FavoriteDto> addRecipe(String recipeId) {
        return Mono.just(recipeId)
                .filterWhen(this::notContainsRecipe)
                .doOnNext(favoriteRecipes::add)
                .thenReturn(this);
    }

    public Mono<FavoriteDto> removeRecipe(String recipeId) {
        return Mono.just(recipeId)
                .doOnNext(favoriteRecipes::remove)
                .thenReturn(this);
    }

    public Mono<Boolean> containsRecipe(String recipeId) {
        return Mono.just(favoriteRecipes.contains(recipeId));
    }

    public Mono<Boolean> notContainsRecipe(String recipeId) {
        return containsRecipe(recipeId).map(exists -> !exists);
    }

    // Métodos para favoriteIngredients
    public Mono<FavoriteDto> addIngredient(String ingredientId) {
        return Mono.just(ingredientId)
                .filterWhen(this::notContainsFavoriteIngredient)
                .doOnNext(favoriteIngredients::add)
                .thenReturn(this);
    }

    public Mono<FavoriteDto> removeIngredient(String ingredientId) {
        return Mono.just(ingredientId)
                .doOnNext(favoriteIngredients::remove)
                .thenReturn(this);
    }

    public Mono<Boolean> containsFavoriteIngredient(String ingredientId) {
        return Mono.just(favoriteIngredients.contains(ingredientId));
    }

    public Mono<Boolean> notContainsFavoriteIngredient(String ingredientId) {
        return containsFavoriteIngredient(ingredientId).map(exists -> !exists);
    }
}
