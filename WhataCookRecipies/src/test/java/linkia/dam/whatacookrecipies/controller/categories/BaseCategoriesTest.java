package linkia.dam.whatacookrecipies.controller.categories;

import linkia.dam.whatacookrecipies.controller.BaseConfigurationTest;
import linkia.dam.whatacookrecipies.controller.CategoryController;
import linkia.dam.whatacookrecipies.model.CategoryDto;
import linkia.dam.whatacookrecipies.service.CategoryService;
import linkia.dam.whatacookrecipies.service.contracts.CategoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.ArrayList;
import java.util.List;

@WebFluxTest(CategoryController.class)
@Import(CategoryService.class)
public class BaseCategoriesTest extends BaseConfigurationTest {

    @Autowired
    protected WebTestClient webTestClient;
    @Autowired
    protected CategoryService categoryService;
    @MockBean
    protected CategoryDao categoryDao;
    @Value("${app.endpoint.categories}")
    protected String categoriesUri;

    public final List<CategoryDto> categoryDtoList = generateCategoryDtoList();
    protected CategoryDto categoryDto;

    protected List<CategoryDto> generateCategoryDtoList() {
        List<CategoryDto> categoryDtoList = new ArrayList<>();
        for (int i = 1; i <= amount; i++) {
            CategoryDto categoryDto = new CategoryDto();
            String prefix = getPrefix(i);
            categoryDto.setId("id-" + prefix + (i % 10 == 0 ? 10 : i % 10));
            categoryDto.setName("Category-" + prefix + (i % 10 == 0 ? 10 : i % 10));
            categoryDtoList.add(categoryDto);
        }
        return categoryDtoList;
    }

    private String getPrefix(int i) {
        if (i <= 10) {
            return "A";
        } else if (i <= 20) {
            return "B";
        } else if (i <= 30) {
            return "C";
        } else {
            return "D";
        }
    }

    protected CategoryDto getExpectedCategoryDto(boolean desc, List<CategoryDto> otherCategoryDtoList) {
        List<CategoryDto> listToSort = (otherCategoryDtoList == null) ? categoryDtoList : otherCategoryDtoList;
        return getExpectedDto(desc, listToSort);
    }

    protected CategoryDto generateCategoryDto() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId("id-A1");
        categoryDto.setName("Category-A1");
        return categoryDto;
    }

}
