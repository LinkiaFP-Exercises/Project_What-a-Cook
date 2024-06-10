package linkia.dam.whatacookrecipies.controller.measures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class TestGetMeasureById extends BaseMeasureTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        measureDto = generateMeasureDto();
        pathVariable = "/id/{id}";
        valuePathVariable = measureDto.getId();
    }

    @Test
    void getMeasureByIdFound() {
        when(measureDao.findById(anyString())).thenReturn(Mono.just(measureDto));
        TestGetMeasureByPathVariableFound(pathVariable, valuePathVariable);
    }

    @Test
    void getMeasureByIdNotFound() {
        when(measureDao.findById(anyString())).thenReturn(Mono.empty());
        TestGetMeasureByPathVariableNotFound(pathVariable, valuePathVariable);
    }
}
