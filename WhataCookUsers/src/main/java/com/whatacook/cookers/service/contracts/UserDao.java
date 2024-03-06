package com.whatacook.cookers.service.contracts;

import com.whatacook.cookers.model.users.UserDTO;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserDao extends ReactiveMongoRepository<UserDTO, String> {

    Mono<UserDTO> findByEmail(String email);
    Mono<UserDTO> findBy_id(String _id);
    Mono<Boolean> existsByEmail(String email);

}
