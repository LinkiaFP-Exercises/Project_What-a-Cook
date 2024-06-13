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
    private List<String> recipes;
    private List<String> ingredients;

    // Métodos para favoriteRecipes
    public Mono<FavoriteDto> addRecipe(String recipeId) {
        return Mono.just(recipeId)
                .filterWhen(this::notContainsRecipe)
                .doOnNext(recipes::add)
                .thenReturn(this);
    }

    public Mono<FavoriteDto> removeRecipe(String recipeId) {
        return Mono.just(recipeId)
                .doOnNext(recipes::remove)
                .thenReturn(this);
    }

    public Mono<Boolean> containsRecipe(String recipeId) {
        return Mono.just(recipes.contains(recipeId));
    }

    public Mono<Boolean> notContainsRecipe(String recipeId) {
        return containsRecipe(recipeId).map(exists -> !exists);
    }

    // Métodos para favoriteIngredients
    public Mono<FavoriteDto> addIngredient(String ingredientId) {
        return Mono.just(ingredientId)
                .filterWhen(this::notContainsFavoriteIngredient)
                .doOnNext(ingredients::add)
                .thenReturn(this);
    }

    public Mono<FavoriteDto> removeIngredient(String ingredientId) {
        return Mono.just(ingredientId)
                .doOnNext(ingredients::remove)
                .thenReturn(this);
    }

    public Mono<Boolean> containsFavoriteIngredient(String ingredientId) {
        return Mono.just(ingredients.contains(ingredientId));
    }

    public Mono<Boolean> notContainsFavoriteIngredient(String ingredientId) {
        return containsFavoriteIngredient(ingredientId).map(exists -> !exists);
    }
}
