package linkia.dam.whatacookrecipies.controller.categories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class TestGetCategoryById extends BaseCategoriesTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryDto = generateCategoryDto();
        pathVariable = "/id/{id}";
        valuePathVariable = categoryDto.getId();
    }

    @Test
    void getCategoryByIdFound() {
        when(categoryDao.findById(anyString())).thenReturn(Mono.just(categoryDto));
        TestGetCategoryByPathVariableFound(pathVariable, valuePathVariable);
    }

    @Test
    void getCategoryByIdNotFound() {
        when(categoryDao.findById(anyString())).thenReturn(Mono.empty());
        TestGetCategoryByPathVariableNotFound(pathVariable, valuePathVariable);
    }

}
