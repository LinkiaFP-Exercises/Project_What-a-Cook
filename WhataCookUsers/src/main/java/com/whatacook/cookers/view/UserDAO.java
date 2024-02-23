package com.whatacook.cookers.view;

import com.whatacook.cookers.model.users.UserDTO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Repository
public interface UserDAO extends ReactiveMongoRepository<UserDTO, String> {

    Mono<UserDTO> findByEmail(String email);
    Mono<Boolean> existsByEmail(String email);

}
