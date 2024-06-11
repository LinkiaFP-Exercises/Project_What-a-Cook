package linkia.dam.whatacookrecipies.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "recipe")
public class RecipeDto extends NamedEntity {

    @DBRef
    private List<IngredientDto> ingredients;

    @DBRef
    private List<CategoryDto> categories;

    private String preparation;
    private int portion;

    public RecipeDto(String id, @NotBlank(message = "name is mandatory") String name, List<IngredientDto> ingredients,
                     List<CategoryDto> categories, String preparation, int portion) {
        super(id, name);
        this.ingredients = ingredients;
        this.categories = categories;
        this.preparation = preparation;
        this.portion = portion;
    }

}
