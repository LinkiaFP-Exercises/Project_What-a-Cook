package com.whatacook.cookers.service.contracts;

import com.whatacook.cookers.model.auth.ResetDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ResetDao extends ReactiveMongoRepository<ResetDto, String> {

    Mono<ResetDto> findByCode(String code);

}
