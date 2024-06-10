package linkia.dam.whatacookrecipies.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@Document(collection = "measure")
public class MeasureDto extends NamedEntity {

    @Id
    private String id;

    @NotBlank(message = "name is mandatory")
    private String name;

}
