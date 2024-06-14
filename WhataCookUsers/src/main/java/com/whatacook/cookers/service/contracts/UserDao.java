package com.whatacook.cookers.service.contracts;

import com.whatacook.cookers.model.users.UserDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserDao extends ReactiveMongoRepository<UserDto, String> {

    Mono<UserDto> findByEmail(String email);
    Mono<UserDto> findBy_id(String _id);
    Mono<Boolean> existsByEmail(String email);

}
