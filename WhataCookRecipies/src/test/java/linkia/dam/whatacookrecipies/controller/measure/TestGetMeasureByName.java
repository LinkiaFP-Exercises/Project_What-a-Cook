package linkia.dam.whatacookrecipies.controller.measure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class TestGetMeasureByName extends BaseMeasureTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        measureDto = generateMeasureDto();
        pathVariable = "/name/{name}";
        valuePathVariable = measureDto.getName();
    }

    @Test
    void getMeasureByNameFound() {
        when(measureDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.just(measureDto));
        TestGetMeasureByPathVariableFound(pathVariable, valuePathVariable);
    }

    @Test
    void getMeasureByNameNotFound() {
        when(measureDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        TestGetMeasureByPathVariableNotFound(pathVariable, valuePathVariable);
    }

}
