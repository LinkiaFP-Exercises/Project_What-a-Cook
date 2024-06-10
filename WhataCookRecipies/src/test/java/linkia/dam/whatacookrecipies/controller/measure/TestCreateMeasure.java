package linkia.dam.whatacookrecipies.controller.measure;

import linkia.dam.whatacookrecipies.model.MeasureDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

public class TestCreateMeasure extends BaseMeasureTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        measureDto = generateMeasureDto();
    }

    @Test
    void createMeasure() {
        when(measureDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(measureDao.save(any(MeasureDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        verifyCreationMeasure();

        verify(measureDao, times(1)).save(any(MeasureDto.class));
    }

    @Test
    void createMeasureAlreadyExists() {
        when(measureDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.just(measureDto));

        verifyCreationMeasure();

        verify(measureDao, times(0)).save(any(MeasureDto.class));
    }

    private void verifyCreationMeasure() {
        webTestClient.post()
                .uri(measuresUri)
                .contentType(APPLICATION_JSON)
                .body(fromValue(measureDto))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(measureDto.getId())
                .jsonPath("$.name").isEqualTo(measureDto.getName());
    }
}
