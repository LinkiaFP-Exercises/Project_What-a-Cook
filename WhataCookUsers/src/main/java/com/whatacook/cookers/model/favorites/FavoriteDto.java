package com.whatacook.cookers.model.favorites;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Data Transfer Object (DTO) for Favorite entity.
 * Represents the favorite recipes and ingredients for a user.
 * <p>
 * Annotations:
 * - @NoArgsConstructor: Generates a no-arguments constructor.
 * - @AllArgsConstructor: Generates an all-arguments constructor.
 * - @Getter: Generates getter methods for all fields.
 * - @Setter: Generates setter methods for all fields.
 * - @ToString: Generates a toString method for the class.
 * - @EqualsAndHashCode: Generates equals and hashCode methods for the class.
 * - @Document: Marks this class as a MongoDB document.
 * <p>
 * Fields:
 * - id: The unique identifier for the favorite entity, same of {@code UserDto}.
 * - recipes: A list of recipe IDs that are marked as favorites.
 * - ingredients: A list of ingredient IDs that are marked as favorites.
 * <p>
 * Methods:
 * - addRecipe(String recipeId): Adds a recipe to the list of favorite recipes.
 * - removeRecipe(String recipeId): Removes a recipe from the list of favorite recipes.
 * - containsRecipe(String recipeId): Checks if a recipe is in the list of favorite recipes.
 * - notContainsRecipe(String recipeId): Checks if a recipe is not in the list of favorite recipes.
 * - addIngredient(String ingredientId): Adds an ingredient to the list of favorite ingredients.
 * - removeIngredient(String ingredientId): Removes an ingredient from the list of favorite ingredients.
 * - containsFavoriteIngredient(String ingredientId): Checks if an ingredient is in the list of favorite ingredients.
 * - notContainsFavoriteIngredient(String ingredientId): Checks if an ingredient is not in the list of favorite ingredients.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
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

    /**
     * Adds a recipe to the list of favorite recipes if not already present.
     *
     * @param recipeId The ID of the recipe to add.
     * @return A Mono emitting this FavoriteDto.
     */
    public Mono<FavoriteDto> addRecipe(String recipeId) {
        return Mono.just(recipeId)
                .filterWhen(this::notContainsRecipe)
                .doOnNext(recipes::add)
                .thenReturn(this);
    }

    /**
     * Removes a recipe from the list of favorite recipes.
     *
     * @param recipeId The ID of the recipe to remove.
     * @return A Mono emitting this FavoriteDto.
     */
    public Mono<FavoriteDto> removeRecipe(String recipeId) {
        return Mono.just(recipeId)
                .doOnNext(recipes::remove)
                .thenReturn(this);
    }

    /**
     * Checks if a recipe is in the list of favorite recipes.
     *
     * @param recipeId The ID of the recipe to check.
     * @return A Mono emitting true if the recipe is in the list, otherwise false.
     */
    public Mono<Boolean> containsRecipe(String recipeId) {
        return Mono.just(recipes.contains(recipeId));
    }

    /**
     * Checks if a recipe is not in the list of favorite recipes.
     *
     * @param recipeId The ID of the recipe to check.
     * @return A Mono emitting true if the recipe is not in the list, otherwise false.
     */
    public Mono<Boolean> notContainsRecipe(String recipeId) {
        return containsRecipe(recipeId).map(exists -> !exists);
    }

    /**
     * Adds an ingredient to the list of favorite ingredients if not already present.
     *
     * @param ingredientId The ID of the ingredient to add.
     * @return A Mono emitting this FavoriteDto.
     */
    public Mono<FavoriteDto> addIngredient(String ingredientId) {
        return Mono.just(ingredientId)
                .filterWhen(this::notContainsFavoriteIngredient)
                .doOnNext(ingredients::add)
                .thenReturn(this);
    }

    /**
     * Removes an ingredient from the list of favorite ingredients.
     *
     * @param ingredientId The ID of the ingredient to remove.
     * @return A Mono emitting this FavoriteDto.
     */
    public Mono<FavoriteDto> removeIngredient(String ingredientId) {
        return Mono.just(ingredientId)
                .doOnNext(ingredients::remove)
                .thenReturn(this);
    }

    /**
     * Checks if an ingredient is in the list of favorite ingredients.
     *
     * @param ingredientId The ID of the ingredient to check.
     * @return A Mono emitting true if the ingredient is in the list, otherwise false.
     */
    public Mono<Boolean> containsFavoriteIngredient(String ingredientId) {
        return Mono.just(ingredients.contains(ingredientId));
    }

    /**
     * Checks if an ingredient is not in the list of favorite ingredients.
     *
     * @param ingredientId The ID of the ingredient to check.
     * @return A Mono emitting true if the ingredient is not in the list, otherwise false.
     */
    public Mono<Boolean> notContainsFavoriteIngredient(String ingredientId) {
        return containsFavoriteIngredient(ingredientId).map(exists -> !exists);
    }
}
