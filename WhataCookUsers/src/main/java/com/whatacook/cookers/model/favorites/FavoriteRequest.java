package com.whatacook.cookers.model.favorites;

import com.whatacook.cookers.model.users.UserJson;
import lombok.*;

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

    public String get_id() {
        return userId;
    }
}
