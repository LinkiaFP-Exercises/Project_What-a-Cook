package com.whatacook.cookers.view;

import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.model.users.UserJson;
import org.springframework.security.core.userdetails.UserDetailsService;

public sealed interface UserAccessContractModel extends UserDetailsService permits UserService {

    public Response existsByEmail(UserJson userJson);

    public Response createOne(UserJson userJson);

    public Response readOne(UserJson userJson);

    public Response updateOne(UserJson userJson);

    public Response deleteOne(UserJson userJson);

}
