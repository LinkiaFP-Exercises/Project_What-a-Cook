package linkia.dam.whatacookrecipes.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Data Transfer Object (DTO) representing an ingredient.
 * Extends the {@link NamedEntity} class to include a name and id.
 * <p>
 * Annotations:
 * - @NoArgsConstructor: Generates a no-argument constructor.
 * - @Getter: Generates getter methods for all fields.
 * - @Setter: Generates setter methods for all fields.
 * - @ToString(callSuper = true): Generates a toString method including fields from the superclass.
 * - @EqualsAndHashCode(callSuper = true): Generates equals and hashCode methods including fields from the superclass.
 * - @Document: Specifies the MongoDB collection name for this entity.
 * <p>
 * Fields:
 * - quantity: The quantity of the ingredient.
 * - measure: The measure associated with the ingredient.
 * <p>
 * Constructor:
 * - IngredientDto(String id, @NotBlank(message = "name is mandatory") String name, double quantity, MeasureDto measure):
 * Constructs an IngredientDto with specified id, name, quantity, and measure.
 *
 * @param id       The unique identifier of the ingredient.
 * @param name     The name of the ingredient, which is mandatory.
 * @param quantity The quantity of the ingredient.
 * @param measure  The measure associated with the ingredient.
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see NamedEntity
 */
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "ingredient")
public class IngredientDto extends NamedEntity {

    private double quantity;

    private MeasureDto measure;

    /**
     * Constructs an IngredientDto with specified id, name, quantity, and measure.
     *
     * @param id       The unique identifier of the ingredient.
     * @param name     The name of the ingredient, which is mandatory.
     * @param quantity The quantity of the ingredient.
     * @param measure  The measure associated with the ingredient.
     */
    public IngredientDto(String id, @NotBlank(message = "name is mandatory") String name, double quantity, MeasureDto measure) {
        super(id, name);
        this.quantity = quantity;
        this.measure = measure;
    }

}
