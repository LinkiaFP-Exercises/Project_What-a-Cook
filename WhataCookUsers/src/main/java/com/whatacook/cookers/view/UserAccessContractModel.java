package com.whatacook.cookers.view;

import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.model.users.UserJustToSave;
import org.springframework.security.core.userdetails.UserDetailsService;

public sealed interface UserAccessContractModel extends UserDetailsService permits UserService {

    Response existsByEmail(UserJson userJson);

    Response createOne(UserJustToSave userJson);

    Response readOne(UserJson userJson);

    Response updateOne(UserJson userJson);

    Response deleteOne(UserJson userJson);

}
