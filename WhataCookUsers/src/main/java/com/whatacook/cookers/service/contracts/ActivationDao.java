package com.whatacook.cookers.service.contracts;

import com.whatacook.cookers.model.auth.ActivationDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ActivationDao  extends ReactiveMongoRepository<ActivationDto, String> {

    Mono<ActivationDto> findByCode(String code);

}
