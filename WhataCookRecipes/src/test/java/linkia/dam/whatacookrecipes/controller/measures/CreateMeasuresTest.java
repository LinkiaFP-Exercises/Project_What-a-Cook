package linkia.dam.whatacookrecipes.controller.measures;

import linkia.dam.whatacookrecipes.model.MeasureDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CreateMeasuresTest extends BaseMeasureTest {

    private Flux<MeasureDto> measureDtoFlux;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        amount = measureDtoList.size();
        measureDtoFlux = Flux.fromIterable(measureDtoList);
    }

    @Test
    void createCategoriesNew() {
        when(measureDao.findByNameIgnoreCase(anyString())).thenReturn(Mono.empty());
        when(measureDao.save(any(MeasureDto.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        testCreateCategories();
    }

    @Test
    void createCategoriesExistingCategories() {
        when(measureDao.findByNameIgnoreCase(anyString())).thenAnswer(invocation -> {
            String name = invocation.getArgument(0);
            return measureDtoList.stream()
                    .filter(measureDto -> measureDto.getName().equalsIgnoreCase(name))
                    .findFirst()
                    .map(Mono::just)
                    .orElse(Mono.empty());
        });

        testCreateCategories();

        verify(measureDao, times(0)).save(any(MeasureDto.class));
    }

    private void testCreateCategories() {
        webTestClient.post()
                .uri(measuresUri + PATH_Bulk)
                .body(measureDtoFlux, MeasureDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(MeasureDto.class)
                .hasSize(amount)
                .value(measures -> {
                    List<String> responseNames = measures.stream().map(MeasureDto::getName).toList();
                    assert responseNames.contains(measureDtoList.getFirst().getName());
                    assert responseNames.contains(measureDtoList.get(amount - 1).getName());
                });
    }
}
