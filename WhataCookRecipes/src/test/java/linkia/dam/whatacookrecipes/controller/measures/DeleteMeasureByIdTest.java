package linkia.dam.whatacookrecipes.controller.measures;

import linkia.dam.whatacookrecipes.model.MeasureDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class DeleteMeasureByIdTest extends BaseMeasureTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        measureDto = generateMeasureDto();
        pathVariable = measuresUri + "/{id}";
        valuePathVariable = measureDto.getId();
    }

    @Test
    void testDeleteMeasureByIdFounded() {
        when(measureDao.findById(anyString())).thenReturn(Mono.just(measureDto));
        when(measureDao.delete(measureDto)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder.path(pathVariable)
                        .build(valuePathVariable))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(response -> {
                    assert response.contains(measureDto.getName());
                    assert response.contains(DELETED);
                });
    }

    @Test
    void testDeleteMeasureByIdNotFound() {
        when(measureDao.findById(anyString())).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(uriBuilder -> uriBuilder.path(pathVariable)
                        .build(valuePathVariable))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();

        verify(measureDao, times(0)).delete(any(MeasureDto.class));
    }
}
