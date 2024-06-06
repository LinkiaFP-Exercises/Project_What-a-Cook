package linkia.dam.whatacookrecipies.service;

import linkia.dam.whatacookrecipies.service.contracts.RecipeDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class RecipeService {

    private final RecipeDao recipeDao;

}
