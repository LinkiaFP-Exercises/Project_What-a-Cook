package com.whatacook.cookers.view;

import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.model.users.UserJustToSave;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.whatacook.cookers.model.responses.Response.error;
import static com.whatacook.cookers.model.responses.Response.success;
import static com.whatacook.cookers.utilities.Util.msgError;

@Service
public final class UserService implements UserAccessContractModel {
    private final ServiceComponentToSave create;
    private final ServiceComponentToFind read;
    private final ServiceComponentToUpdate update;
    private final ServiceComponentToDelete delete;
    private final ServiceComponentToLogin login;
    private final ServiceComponentToActivate activate;

    public UserService(ServiceComponentToSave create, ServiceComponentToFind read, ServiceComponentToUpdate update,
                       ServiceComponentToDelete delete, ServiceComponentToLogin login, ServiceComponentToActivate activate) {
        this.create = create;
        this.read = read;
        this.update = update;
        this.delete = delete;
        this.login = login;
        this.activate = activate;
    }

    @Override
    public Response existsByEmail(UserJson userJson) {
        Response response = error(msgError("FIND IF EXISTS a User by e-mail"));

        try {
            response = read.checkIfExistsByEmail(userJson)
                    .map(alreadyExists -> alreadyExists
                            ? success("User already exists", true)
                            : success("User does not exist yet", false))
                    .block();
        }
        catch (Exception e) { response.addMessage(e.getMessage()); }

        return response;
    }

    @Override
    public Response createOne(UserJustToSave userJson) {
        Response response = error(msgError("CREATE a User"));

        try {
            response = create.saveUser(userJson)
                    .map(saved ->
                            success("User successfully created", saved))
                    .block();
        }
        catch (UserServiceException e) { response = error(e.getMessage(), e.getErrors()); }
        catch (Exception e) { response.addMessage(e.getMessage()); }

        return response;
    }

    public Mono<Response> activateAccount(String activationCode) {
        Mono<Response> response;

        try {
            response = activate.byActivationCodeSentByEmail(activationCode)
                    .map(htmlContent -> success("Account successfully activated", htmlContent));
        }
        catch (UserServiceException e) { response = Mono.just(error(e.getMessage(), e.getErrors())); }
        catch (Exception e) { response = Mono.just(error(e.getMessage())); }

        return response;
    }
    public Mono<Response> resendActivateCode(String email) {
        Mono<Response> response;

        try {
            response = activate.resendActivationCode(email)
                    .map(resended -> success("Activation mail successfully resented", resended));
        }
        catch (UserServiceException e) { response = Mono.just(error(e.getMessage(), e.getErrors())); }
        catch (Exception e) { response = Mono.just(error(e.getMessage())); }

        return response;
    }

    @Override
    public Mono<Response> readOne(UserJson userJson) {
        Mono<Response> response;

        try {
            response = read.findUserByEmail(userJson)
                    .map(found ->
                            success("Player successfully read", found));
        }
        catch (Exception e) { response = Mono.just(error(e.getMessage())); }

        return response;
    }

    @Override
    public Response updateOne(UserJson userJson) {
        Response response = error(msgError("UPDATE a User"));

        try {
            response = update.updateUser(userJson)
                    .map(updated ->
                            success("Player successfully UPDATE", updated))
                    .block();
        }
        catch (UserServiceException e) { response = error(e.getMessage(), e.getErrors()); }
        catch (Exception e) { response.addMessage(e.getMessage()); }

        return response;
    }

    @Override
    public Response deleteOne(UserJson userJson) {
        Response response = error(msgError("DELETE a User"));

        try {
            response = delete.proceedIfApplicable(userJson).block();
        }
        catch (Exception e) {response.addMessage(e.getMessage());}

        return response;
    }

    @Override
    public Mono<UserDetails> findByUsername(String userEmailOrId) throws UsernameNotFoundException {
        try {
            return login.validSpringUserToLogin(userEmailOrId);
        } catch (Exception e) { throw new UsernameNotFoundException(userEmailOrId); }
    }

}
