package com.whatacook.cookers.view;

import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.model.users.UserJustToSave;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import reactor.core.publisher.Mono;

public sealed interface UserAccessContractModel extends ReactiveUserDetailsService permits UserService {

    Mono<Response> existsByEmail(UserJson userJson);

    Response createOne(UserJustToSave userJson);

    Mono<Response> readOne(UserJson userJson);

    Response updateOne(UserJson userJson);

    Response deleteOne(UserJson userJson);

}
