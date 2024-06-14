package linkia.dam.whatacookrecipes.model;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a recipe.
 * Extends the {@link NamedEntity} class to include a name and id.
 * <p>
 * Annotations:
 * - @NoArgsConstructor: Generates a no-argument constructor.
 * - @AllArgsConstructor: Generates an all-arguments constructor.
 * - @Getter: Generates getter methods for all fields.
 * - @Setter: Generates setter methods for all fields.
 * - @ToString(callSuper = true): Generates a toString method including fields from the superclass.
 * - @EqualsAndHashCode(callSuper = true): Generates equals and hashCode methods including fields from the superclass.
 * - @Document: Specifies the MongoDB collection name for this entity.
 * <p>
 * Fields:
 * - ingredients: The list of ingredients used in the recipe.
 * - categories: The list of categories to which the recipe belongs.
 * - preparation: The preparation instructions for the recipe.
 * - portion: The number of portions the recipe yields.
 * <p>
 * Constructors:
 * - RecipeDto(String id, @NotBlank(message = "name is mandatory") String name, List<IngredientDto> ingredients,
 * List<CategoryDto> categories, String preparation, int portion): Constructs a RecipeDto with specified id, name,
 * ingredients, categories, preparation, and portion.
 * - RecipeDto(): Default no-argument constructor.
 * - RecipeDto(List<IngredientDto> ingredients, List<CategoryDto> categories, String preparation, int portion): Constructs
 * a RecipeDto with specified ingredients, categories, preparation, and portion.
 *
 * @Author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see NamedEntity
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Document(collection = "recipe")
public class RecipeDto extends NamedEntity {

    private List<IngredientDto> ingredients;

    private List<CategoryDto> categories;

    private String preparation;
    private int portion;

    /**
     * Constructs a RecipeDto with specified id, name, ingredients, categories, preparation, and portion.
     *
     * @param id          The unique identifier of the recipe.
     * @param name        The name of the recipe, which is mandatory.
     * @param ingredients The list of ingredients used in the recipe.
     * @param categories  The list of categories to which the recipe belongs.
     * @param preparation The preparation instructions for the recipe.
     * @param portion     The number of portions the recipe yields.
     */
    public RecipeDto(String id, @NotBlank(message = "name is mandatory") String name, List<IngredientDto> ingredients,
                     List<CategoryDto> categories, String preparation, int portion) {
        super(id, name);
        this.ingredients = ingredients;
        this.categories = categories;
        this.preparation = preparation;
        this.portion = portion;
    }

}
