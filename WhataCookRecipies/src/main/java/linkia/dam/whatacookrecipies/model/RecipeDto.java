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
@ToString
@EqualsAndHashCode(callSuper = true)
@Document(collection = "recipe")
public class RecipeDto extends NamedEntity {

    @Id
    private String id;

    @NotBlank(message = "name is mandatory")
    private String name;

    @DBRef
    private List<IngredientDto> ingredients;

    @DBRef
    private List<CategoryDto> categories;

    private String preparation;
    private int portion;

}
