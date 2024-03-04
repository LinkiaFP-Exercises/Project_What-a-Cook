package com.whatacook.cookers.service.contracts;

import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.model.users.UserJustToSave;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Mono;

public interface UserAccessContractModel extends ReactiveUserDetailsService {

    Mono<Response> existsByEmail(UserJson userJson);

    Mono<Response> createOne(UserJustToSave userJson);

    Mono<Response> readOne(UserJson userJson);

    Mono<Response> updateOne(UserJson userJson);

    Mono<Response> deleteOne(UserJson userJson);

}
