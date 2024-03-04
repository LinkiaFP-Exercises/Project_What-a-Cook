package com.whatacook.cookers.service;

import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.model.users.UserJustToSave;
import com.whatacook.cookers.service.components.*;
import com.whatacook.cookers.service.contracts.UserAccessContractModel;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.whatacook.cookers.model.responses.Response.success;

@AllArgsConstructor
@Service
public final class UserService implements UserAccessContractModel {
    private final SaveComponent create;
    private final FindComponent read;
    private final UpdateComponent update;
    private final DeleteComponent delete;
    private final LoginComponent login;
    private final ActivateComponent activate;


    @Override
    public Mono<Response> existsByEmail(UserJson userJson) {
        return read.checkIfExistsByEmail(userJson)
                    .map(alreadyExists -> alreadyExists
                            ? success("User already exists", true)
                            : success("User does not exist yet", false))
                .onErrorResume(UserServiceException.class, Response::monoError)
                .onErrorResume(Exception.class, Response::monoError);
    }

    @Override
    public Mono<Response> createOne(UserJustToSave userJson) {
        return create.saveUser(userJson)
                .map(saved -> success("User successfully created", saved))
                .onErrorResume(UserServiceException.class, Response::monoError)
                .onErrorResume(Exception.class, Response::monoError);
    }

    public Mono<Response> activateAccount(String activationCode) {
        return activate.byActivationCodeSentByEmail(activationCode)
                .map(htmlContent -> success("Account successfully activated", htmlContent))
                .onErrorResume(UserServiceException.class, Response::monoError)
                .onErrorResume(Exception.class, Response::monoError);

    }

    public Mono<Response> resendActivateCode(String email) {
        return activate.resendActivationCode(email)
                    .map(resended -> success("Activation mail successfully resented", resended))
                    .onErrorResume(UserServiceException.class, Response::monoError)
                    .onErrorResume(Exception.class, Response::monoError);

    }

    @Override
    public Mono<Response> readOne(UserJson userJson) {
        return read.findUserByEmail(userJson)
                    .map(found ->
                            success("Player successfully read", found))
                .onErrorResume(UserServiceException.class, Response::monoError)
                .onErrorResume(Exception.class, Response::monoError);

    }

    @Override
    public Mono<Response> updateOne(UserJson userJson) {
        return update.updateUser(userJson)
                    .map(updated ->
                            success("Player successfully UPDATE", updated))
                    .onErrorResume(UserServiceException.class, Response::monoError)
                    .onErrorResume(Exception.class, Response::monoError);
    }

    @Override
    public Mono<Response> deleteOne(UserJson userJson) {
        return delete.proceedIfApplicable(userJson)
                    .onErrorResume(UserServiceException.class, Response::monoError)
                    .onErrorResume(Exception.class, Response::monoError);
    }

    @Override
    public Mono<UserDetails> findByUsername(String userEmailOrId) throws UsernameNotFoundException {
        try {
            return login.validSpringUserToLogin(userEmailOrId);
        } catch (Exception e) {
            throw new UsernameNotFoundException(userEmailOrId);
        }
    }

}