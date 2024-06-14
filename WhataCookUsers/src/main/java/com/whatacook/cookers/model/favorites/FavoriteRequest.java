package com.whatacook.cookers.model.favorites;

import com.whatacook.cookers.model.users.UserJson;
import lombok.*;

/**
 * Request object for managing user favorites.
 * Inherits from UserJson and adds fields for user ID, recipe ID, and ingredient ID.
 * <p>
 * Annotations:
 * - @NoArgsConstructor: Generates a no-arguments constructor.
 * - @AllArgsConstructor: Generates an all-arguments constructor.
 * - @Getter: Generates getter methods for all fields.
 * - @Setter: Generates setter methods for all fields.
 * - @ToString: Generates a toString method for the class.
 * - @EqualsAndHashCode: Generates equals and hashCode methods for the class.
 * <p>
 * Fields:
 * - userId: The unique identifier for the user.
 * - recipeId: The ID of the recipe to add/remove from favorites.
 * - ingredientId: The ID of the ingredient to add/remove from favorites.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class FavoriteRequest extends UserJson {

    private String userId;
    private String recipeId;
    private String ingredientId;

    /**
     * Gets the user ID.
     *
     * @return The user ID.
     */
    public String get_id() {
        return userId;
    }
}
