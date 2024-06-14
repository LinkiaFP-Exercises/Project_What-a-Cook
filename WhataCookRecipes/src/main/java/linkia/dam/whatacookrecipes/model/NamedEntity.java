package linkia.dam.whatacookrecipes.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Base entity class with an ID and a name attribute.
 * <p>
 * Annotations:
 * - @NoArgsConstructor: Generates a no-argument constructor.
 * - @AllArgsConstructor: Generates an all-arguments constructor.
 * - @Getter: Generates getter methods for all fields.
 * - @Setter: Generates setter methods for all fields.
 * - @ToString: Generates a toString method.
 * - @EqualsAndHashCode: Generates equals and hashCode methods.
 * <p>
 * Fields:
 * - id: The unique identifier of the entity.
 * - name: The name of the entity, which is mandatory and indexed uniquely.
 * <p>
 * Additional Annotations:
 * - @Id: Marks the field as the primary key in the MongoDB document.
 * - @Field: Specifies the field name in the MongoDB document.
 * - @NotBlank: Validates that the field is not null and not blank.
 * - @Indexed: Marks the field as indexed, ensuring it is unique in the database.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class NamedEntity {
    /**
     * The unique identifier of the entity.
     */
    @Id
    protected String id;

    /**
     * The name of the entity, which is mandatory and indexed uniquely.
     */
    @Field("name")
    @NotBlank(message = "name is mandatory")
    @Indexed(unique = true)
    protected String name;
}
