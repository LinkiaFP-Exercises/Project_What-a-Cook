package linkia.dam.whatacookrecipes.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Data Transfer Object (DTO) representing a measure.
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
 * Constructor:
 * - MeasureDto(String id, @NotBlank(message = "name is mandatory") String name): Constructs a MeasureDto with specified id and name.
 * <p>
 * Fields inherited from {@link NamedEntity}:
 * - id: The unique identifier of the measure.
 * - name: The name of the measure, which is mandatory.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see NamedEntity
 */
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "measure")
public class MeasureDto extends NamedEntity {
    /**
     * Constructs a MeasureDto with specified id and name.
     *
     * @param id   The unique identifier of the measure.
     * @param name The name of the measure, which is mandatory.
     */
    public MeasureDto(String id, @NotBlank(message = "name is mandatory") String name) {
        super(id, name);
    }

}
