package linkia.dam.whatacookrecipies.controller;

import linkia.dam.whatacookrecipies.model.MeasureDto;
import linkia.dam.whatacookrecipies.service.MeasureService;
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

    @GetMapping("id/{id}")
    public Mono<MeasureDto> getMeasureById(@PathVariable String id) {
        return measureService.getMeasureById(id);
    }

    @GetMapping("name/{name}")
    public Mono<MeasureDto> getMeasureByName(@PathVariable String name) {
        return measureService.getMeasureByName(name);
    }

    @PostMapping
    public Mono<MeasureDto> createMeasure(@RequestBody MeasureDto categoryDto) {
        return measureService.createMeasure(categoryDto);
    }

    @PostMapping("/bulk")
    public Flux<MeasureDto> createMeasures(@RequestBody Flux<MeasureDto> categories) {
        return measureService.createMeasures(categories);
    }

    @DeleteMapping("/{id}")
    public Mono<String> deleteMeasureById(@PathVariable String id) {
        return measureService.deleteMeasure(id);
    }

    @DeleteMapping("/all")
    public Mono<Void> deleteAllMeasures() {
        return measureService.deleteAllMeasures();
    }

}