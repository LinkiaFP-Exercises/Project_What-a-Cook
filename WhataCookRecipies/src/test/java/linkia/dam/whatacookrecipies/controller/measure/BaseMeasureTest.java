package linkia.dam.whatacookrecipies.controller.measure;

import linkia.dam.whatacookrecipies.controller.BaseTestingConfiguration;
import linkia.dam.whatacookrecipies.controller.MeasureController;
import linkia.dam.whatacookrecipies.model.MeasureDto;
import linkia.dam.whatacookrecipies.service.MeasureService;
import linkia.dam.whatacookrecipies.service.contracts.MeasureDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@WebFluxTest(MeasureController.class)
@Import(MeasureService.class)
public class BaseMeasureTest extends BaseTestingConfiguration {

    @Autowired
    protected WebTestClient webTestClient;
    @Autowired
    protected MeasureService measureService;
    @MockBean
    protected MeasureDao measureDao;
    @Value("${app.endpoint.measures}")
    protected String measuresUri;

    protected MeasureDto measureDto;

    protected final List<MeasureDto> measureDtoList = Arrays.asList(
            new MeasureDto("measure-1", "Cucharadita"),
            new MeasureDto("measure-2", "Cucharada"),
            new MeasureDto("measure-3", "Taza"),
            new MeasureDto("measure-4", "Pinta"),
            new MeasureDto("measure-5", "Cuarto de galón"),
            new MeasureDto("measure-6", "Galón"),
            new MeasureDto("measure-7", "Onza"),
            new MeasureDto("measure-8", "Libra"),
            new MeasureDto("measure-9", "Mililitro"),
            new MeasureDto("measure-10", "Litro"),
            new MeasureDto("measure-11", "Gramo"),
            new MeasureDto("measure-12", "Kilogramo"),
            new MeasureDto("measure-13", "Pizca"),
            new MeasureDto("measure-14", "Pellizco"),
            new MeasureDto("measure-15", "Onza líquida"),
            new MeasureDto("measure-16", "Gota"),
            new MeasureDto("measure-17", "Barra"),
            new MeasureDto("measure-18", "Diente"),
            new MeasureDto("measure-19", "Rebanada"),
            new MeasureDto("measure-20", "Pieza")
    );

    protected MeasureDto generateMeasureDto() {
        MeasureDto measureDto = new MeasureDto();
        measureDto.setId(measureDtoList.get(0).getId().replace("1", "X"));
        measureDto.setName(measureDtoList.get(0).getName());
        return measureDto;
    }


    void TestGetMeasureByPathVariableFound(String pathVariable, String valuePathVariable) {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(measuresUri + pathVariable)
                        .build(valuePathVariable))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(measureDto.getId())
                .jsonPath("$.name").isEqualTo(measureDto.getName());
    }

    void TestGetMeasureByPathVariableNotFound(String pathVariable, String valuePathVariable) {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path(measuresUri + pathVariable)
                        .build(valuePathVariable))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
