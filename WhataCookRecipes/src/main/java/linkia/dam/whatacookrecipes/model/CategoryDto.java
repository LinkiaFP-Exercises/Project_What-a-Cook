package linkia.dam.whatacookrecipes.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Data Transfer Object (DTO) representing a category.
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
 * - CategoryDto(String id, @NotBlank(message = "name is mandatory") String name): Constructs a CategoryDto with specified id and name.
 * <p>
 * Fields inherited from {@link NamedEntity}:
 * - id: The unique identifier of the category.
 * - name: The name of the category, which is mandatory.
 *
 * @Author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see NamedEntity
 */
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "category")
public class CategoryDto extends NamedEntity {
    /**
     * Constructs a CategoryDto with specified id and name.
     *
     * @param id   The unique identifier of the category.
     * @param name The name of the category, which is mandatory.
     */
    public CategoryDto(String id, @NotBlank(message = "name is mandatory") String name) {
        super(id, name);
    }

}
