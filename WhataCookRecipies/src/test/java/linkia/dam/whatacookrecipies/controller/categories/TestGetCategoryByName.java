package linkia.dam.whatacookrecipies.controller.categories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class TestGetCategoryByName extends BaseCategoriesTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryDto = generateCategoryDto();
        pathVariable = "/name/{name}";
        valuePathVariable = categoryDto.getName();
    }

    @Test
    void getCategoryByNameFound() {
        when(categoryDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.just(categoryDto));
        TestGetCategoryByPathVariableFound(pathVariable, valuePathVariable);
    }

    @Test
    void getCategoryByNameNotFound() {
        when(categoryDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        TestGetCategoryByPathVariableNotFound(pathVariable, valuePathVariable);
    }

}
