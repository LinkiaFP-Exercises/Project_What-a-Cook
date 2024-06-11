package linkia.dam.whatacookrecipes.controller.categories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GetCategoryByNameTest extends BaseCategoriesTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryDto = generateCategoryDto();
        pathVariable = categoriesUri + PATH_NAME;
        valuePathVariable = categoryDto.getName();
    }

    @Test
    void getCategoryByNameFound() {
        when(categoryDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.just(categoryDto));
        TestGetByPathVariableFounded(webTestClient, pathVariable, valuePathVariable, categoryDto);
    }

    @Test
    void getCategoryByNameNotFound() {
        when(categoryDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        TestGetByPathVariableNotFound(webTestClient, pathVariable, valuePathVariable);
    }

}
