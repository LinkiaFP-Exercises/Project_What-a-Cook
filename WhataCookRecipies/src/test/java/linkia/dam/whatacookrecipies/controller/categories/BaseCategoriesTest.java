package linkia.dam.whatacookrecipies.controller.categories;

import linkia.dam.whatacookrecipies.controller.BaseTestingConfiguration;
import linkia.dam.whatacookrecipies.model.CategoryDto;
import linkia.dam.whatacookrecipies.service.CategoryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

public class BaseCategoriesTest extends BaseTestingConfiguration {

    @MockBean
    protected CategoryService categoryService;

    @Value("${app.endpoint.categories}")
    protected String categoriesUri;

    protected int amount;
    protected List<CategoryDto> categoryDtoList;

    protected List<CategoryDto> generateCategoryDtoList(int amount) {
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        for (int i = 1; i <= amount; i++) {
            CategoryDto categoryDto = new CategoryDto();
            categoryDto.setId("id-" + i);
            categoryDto.setName("Category " + i);
            categoryDtoList.add(categoryDto);
        }
        return categoryDtoList;
    }

}
