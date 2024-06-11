package linkia.dam.whatacookrecipies.controller.categories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GetCategoryByIdTest extends BaseCategoriesTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryDto = generateCategoryDto();
        pathVariable = categoriesUri + PATH_SLASH_ID;
        valuePathVariable = categoryDto.getId();
    }

    @Test
    void getCategoryByIdFound() {
        when(categoryDao.findById(anyString())).thenReturn(Mono.just(categoryDto));
        TestGetByPathVariableFounded(webTestClient, pathVariable, valuePathVariable, categoryDto);
    }

    @Test
    void getCategoryByIdNotFound() {
        when(categoryDao.findById(anyString())).thenReturn(Mono.empty());
        TestGetByPathVariableNotFound(webTestClient, pathVariable, valuePathVariable);
    }

}
