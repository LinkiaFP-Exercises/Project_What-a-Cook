package linkia.dam.whatacookrecipies.controller.categories;

import linkia.dam.whatacookrecipies.controller.BaseTestingConfiguration;
import linkia.dam.whatacookrecipies.model.CategoryDto;
import linkia.dam.whatacookrecipies.service.CategoryService;
import linkia.dam.whatacookrecipies.service.contracts.CategoryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Import(CategoryService.class)
public class BaseCategoriesTest extends BaseTestingConfiguration {

    @Autowired
    protected CategoryService categoryService;
    @MockBean
    protected CategoryDao categoryDao;
    @Value("${app.endpoint.categories}")
    protected String categoriesUri;

    protected int page;
    protected int size;
    protected int amount;
    protected String name;
    protected List<CategoryDto> categoryDtoList;
    protected CategoryDto categoryDto;
    protected String pathVariable, valuePathVariable;

    protected List<CategoryDto> generateCategoryDtoList(int amount) {
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

    protected CategoryDto getExpectedCategoryDto(boolean desc) {
        List<CategoryDto> sortedList = new ArrayList<>(categoryDtoList);
        if (desc) {
            sortedList.sort((a, b) -> b.getName().compareTo(a.getName()));
        } else {
            sortedList.sort(Comparator.comparing(CategoryDto::getName));
        }
        int startIndex = page * size;
        return sortedList.get(startIndex);
    }

    protected int getNumberLastElements() {
        return amount % size == 0 ? size : amount % size;
    }

    protected CategoryDto generateCategoryDto() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId("id-A1");
        categoryDto.setName("Category-A1");
        return categoryDto;
    }

    void TestGetCategoryByPathVariableFound(String pathVariable, String valuePathVariable) {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(categoriesUri + pathVariable)
                        .build(valuePathVariable))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(categoryDto.getId())
                .jsonPath("$.name").isEqualTo(categoryDto.getName());
    }

    void TestGetCategoryByPathVariableNotFound(String pathVariable, String valuePathVariable) {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(categoriesUri + pathVariable)
                        .build(valuePathVariable))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

}
