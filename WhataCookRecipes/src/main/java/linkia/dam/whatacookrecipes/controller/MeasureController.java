package linkia.dam.whatacookrecipes.controller;

import linkia.dam.whatacookrecipes.model.MeasureDto;
import linkia.dam.whatacookrecipes.service.MeasureService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing measures.
 * Provides endpoints for CRUD operations on measures.
 * <p>
 * Annotations:
 * - @AllArgsConstructor: Generates a constructor with 1 parameter for each field in the class.
 * - @RestController: Indicates that this class is a REST controller.
 * - @RequestMapping: Specifies the base URL for all endpoints in this controller.
 * - @Validated: Enables validation for the controller.
 * <p>
 * Methods:
 * - getMeasureById(String id): Retrieves a measure by its ID.
 * - getMeasureByName(String name): Retrieves a measure by its name, case insensitive.
 * - createMeasure(MeasureDto categoryDto): Creates a new measure.
 * - createMeasures(Flux<MeasureDto> categories): Creates multiple measures.
 * - deleteMeasureById(String id): Deletes a measure by its ID.
 * - deleteAllMeasures(): Deletes all measures.
 *
 * @Author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see MeasureService
 */
@AllArgsConstructor
@RestController
@RequestMapping("${app.endpoint.measures}")
@Validated
public class MeasureController {

    private final MeasureService measureService;

    /**
     * Retrieves a measure by its ID.
     *
     * @param id The ID of the measure to retrieve.
     * @return A {@link Mono} emitting the {@link MeasureDto} object, or an error if not found.
     */
    @GetMapping("${app.sub-endpoint.id.path-variable-id}")
    public Mono<MeasureDto> getMeasureById(@PathVariable String id) {
        return measureService.getMeasureById(id);
    }

    /**
     * Retrieves a measure by its name, case insensitive.
     *
     * @param name The name of the measure to retrieve.
     * @return A {@link Mono} emitting the {@link MeasureDto} object, or an error if not found.
     */
    @GetMapping("${app.sub-endpoint.name.path-variable-name}")
    public Mono<MeasureDto> getMeasureByName(@PathVariable String name) {
        return measureService.getMeasureByNameIgnoreCase(name);
    }

    /**
     * Creates a new measure.
     *
     * @param categoryDto The measure data to create.
     * @return A {@link Mono} emitting the created {@link MeasureDto} object.
     */
    @PostMapping
    public Mono<MeasureDto> createMeasure(@RequestBody MeasureDto categoryDto) {
        return measureService.createMeasure(categoryDto);
    }

    /**
     * Creates multiple measures.
     *
     * @param categories The flux of measures to create.
     * @return A {@link Flux} emitting the created {@link MeasureDto} objects.
     */
    @PostMapping("${app.sub-endpoint.bulk}")
    public Flux<MeasureDto> createMeasures(@RequestBody Flux<MeasureDto> categories) {
        return measureService.createMeasures(categories);
    }

    /**
     * Deletes a measure by its ID.
     *
     * @param id The ID of the measure to delete.
     * @return A {@link Mono} emitting a message indicating the measure has been deleted, or an error if not found.
     */
    @DeleteMapping("${app.sub-endpoint.path-variable-id}")
    public Mono<String> deleteMeasureById(@PathVariable String id) {
        return measureService.deleteMeasure(id);
    }

    /**
     * Deletes all measures.
     *
     * @return A {@link Mono} indicating completion of the deletion process.
     */
    @DeleteMapping("${app.sub-endpoint.all}")
    public Mono<Void> deleteAllMeasures() {
        return measureService.deleteAllMeasures();
    }
}
