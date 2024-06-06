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
@EqualsAndHashCode
@Document(collection = "measure")
public class MeasureDto {

    @Id
    private String id;

    @NotBlank(message = "name is mandatory")
    private String name;

}
