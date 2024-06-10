package linkia.dam.whatacookrecipies.service;

import linkia.dam.whatacookrecipies.model.MeasureDto;
import linkia.dam.whatacookrecipies.model.exception.ResourceNotFoundException;
import linkia.dam.whatacookrecipies.service.contracts.MeasureDao;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Service
public class MeasureService {

    private final MeasureDao measureDao;

    public Mono<MeasureDto> getMeasureById(String id) {
        return measureDao.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Measure not found with id=" + id)));
    }
    public Mono<MeasureDto> getMeasureByName(String name) {
        return measureDao.findByNameIgnoreCase(name)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Measure not found with name=" + name)));
    }

    public Mono<MeasureDto> createMeasure(MeasureDto measureDto) {
        return measureDao.findByNameIgnoreCase(measureDto.getName())
                .switchIfEmpty(Mono.defer(() -> measureDao.save(measureDto)));
    }

    public Flux<MeasureDto> createMeasures(Flux<MeasureDto> measures) {
        return measures.flatMap(this::createMeasure);
    }

    public Mono<String> deleteMeasure(String id) {
        return measureDao.findById(id)
                .flatMap(existingMeasure -> measureDao.delete(existingMeasure)
                        .then(Mono.just("Measure " + existingMeasure.getName() + " has been deleted.")))
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Measure not found with id=" + id)));
    }

    public Mono<Void> deleteAllMeasures() {
        return measureDao.deleteAll();
    }

}
