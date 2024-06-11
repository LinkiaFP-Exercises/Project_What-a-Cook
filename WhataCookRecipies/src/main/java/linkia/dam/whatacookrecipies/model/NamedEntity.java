package linkia.dam.whatacookrecipies.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class NamedEntity {
    @Field("id")
    protected String id;

    @Field("name")
    @NotBlank(message = "name is mandatory")
    protected String name;
}
