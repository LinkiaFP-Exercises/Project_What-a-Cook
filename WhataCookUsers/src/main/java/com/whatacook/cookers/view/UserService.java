package com.whatacook.cookers.view;

import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.model.users.UserDTO;
import com.whatacook.cookers.model.users.UserJson;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.whatacook.cookers.model.responses.Response.error;
import static com.whatacook.cookers.model.responses.Response.success;
import static com.whatacook.cookers.utilities.Util.msgError;

@Service
public final class UserService implements UserAccessContractModel {
    private final ServiceComponentToFind service;

    private final ServiceComponentToLogin login;

    private final ServiceComponentToSave save;

    public UserService(ServiceComponentToFind service, ServiceComponentToLogin login, ServiceComponentToSave save) {
        this.service = service;
        this.login = login;
        this.save = save;
    }

    @Override
    public Response existsByEmail(UserJson userJson) {
        Response response = error(msgError("FIND IF EXISTS a User by e-mail"));

        try {

            boolean alreadyExists = service.checkIfExistsByEmail(userJson).block();

            response = (alreadyExists) ? success("User already exists", true)
                    : success("User does not exist yet", false);

        } catch (Exception e) {
            response.addMessage(e.getMessage());
        }

        return response;
    }

    @Override
    public Response createOne(UserJson userJson) {
        Response response = error(msgError("CREATE a User"));

        try {

            UserJson saved = save.saveUser(userJson).block();

            response = success("User successfully created", saved);

        } catch (UserServiceException e) {
            response = error(e.getMessage(), e.getErrors());

        } catch (Exception e) {
            response.addMessage(e.getMessage());

        }

        return response;
    }

    @Override
    public Response readOne(UserJson userJson) {
        Response response = error(msgError("READ a User"));

        try {

            //noinspection SpellCheckingInspection
            UserJson finded = service.findUserByEmail(userJson).block();

            response = success("Player successfully created", finded);

        } catch (Exception e) {
            response.addMessage(e.getMessage());
        }

        return response;
    }

    @Override
    public Response readAll(UserJson userJson) {
        Response response = error(msgError("READ all Users"));

        try {

            response = success("Player successfully created", null);

        } catch (Exception e) {
            response.addMessage(e.getMessage());
        }

        return response;
    }

    @Override
    public Response updateOne(UserJson userJson) {
        Response response = error(msgError("UPDATE a User"));

        try {

            response = success("Player successfully created", null);

        } catch (Exception e) {
            response.addMessage(e.getMessage());
        }

        return response;
    }

    @Override
    public Response deleteOne(UserJson userJson) {
        Response response = error(msgError("DELETE a User"));

        try {

            response = success("Player successfully created", null);

        } catch (Exception e) {
            response.addMessage(e.getMessage());
        }

        return response;
    }

    @Override
    public Response deleteAll(UserJson userJson) {
        Response response = error(msgError("DELETE all Users"));

        try {

            response = success("Player successfully created", null);

        } catch (Exception e) {
            response.addMessage(e.getMessage());
        }

        return response;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        try {

            return login.validSpringUserToLogin(UserDTO.justWithMail(email));

        } catch (Exception e) {
            throw new UsernameNotFoundException(email);
        }

    }

}
