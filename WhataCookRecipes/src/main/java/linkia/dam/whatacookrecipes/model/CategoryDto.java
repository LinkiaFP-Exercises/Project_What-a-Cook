package linkia.dam.whatacookrecipes.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "category")
public class CategoryDto extends NamedEntity {

    public CategoryDto(String id, @NotBlank(message = "name is mandatory") String name) {
        super(id, name);
    }

}
