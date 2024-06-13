package linkia.dam.whatacookrecipes.controller;

import linkia.dam.whatacookrecipes.model.MeasureDto;
import linkia.dam.whatacookrecipes.service.MeasureService;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping("${app.endpoint.measures}")
@Validated
public class MeasureController {
    
    private final MeasureService measureService;

    @GetMapping("${app.sub-endpoint.id.path-variable-id}")
    public Mono<MeasureDto> getMeasureById(@PathVariable String id) {
        return measureService.getMeasureById(id);
    }

    @GetMapping("${app.sub-endpoint.name.path-variable-name}")
    public Mono<MeasureDto> getMeasureByName(@PathVariable String name) {
        return measureService.getMeasureByNameIgnoreCase(name);
    }

    @PostMapping
    public Mono<MeasureDto> createMeasure(@RequestBody MeasureDto categoryDto) {
        return measureService.createMeasure(categoryDto);
    }

    @PostMapping("${app.sub-endpoint.bulk}")
    public Flux<MeasureDto> createMeasures(@RequestBody Flux<MeasureDto> categories) {
        return measureService.createMeasures(categories);
    }

    @DeleteMapping("${app.sub-endpoint.path-variable-id}")
    public Mono<String> deleteMeasureById(@PathVariable String id) {
        return measureService.deleteMeasure(id);
    }

    @DeleteMapping("${app.sub-endpoint.all}")
    public Mono<Void> deleteAllMeasures() {
        return measureService.deleteAllMeasures();
    }

}