package linkia.dam.whatacookrecipes.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "ingredient")
public class IngredientDto extends NamedEntity {

    private double quantity;

    @DBRef
    private MeasureDto measure;

    public IngredientDto(String id, @NotBlank(message = "name is mandatory") String name, double quantity, MeasureDto measure) {
        super(id, name);
        this.quantity = quantity;
        this.measure = measure;
    }

}
