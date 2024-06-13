package com.whatacook.cookers.model.favorites;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class FavoriteRequest {

    private String userId;
    private String recipeId;
    private String ingredientId;
}
