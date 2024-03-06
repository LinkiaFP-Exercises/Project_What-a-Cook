package com.whatacook.cookers.service;

import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.service.components.*;
import com.whatacook.cookers.service.contracts.UserAccessContractModel;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static com.whatacook.cookers.model.responses.Response.error;
import static com.whatacook.cookers.model.responses.Response.success;
import static com.whatacook.cookers.utilities.Util.convertToJsonAsString;

@AllArgsConstructor
@Service
public final class UserService implements UserAccessContractModel {
    private final FindComponent read;
    private final UpdateComponent update;
    private final DeleteComponent delete;
    private final LoginComponent login;
    private final ActivateComponent activate;
    private final ResetComponent resetComponent;


    @Override
    public Mono<Response> existsByEmail(UserJson userJson) {
        return read.checkIfExistsByEmail(userJson)
                .map(alreadyExists -> alreadyExists
                        ? success("User already exists", true)
                        : success("User does not exist yet", false))
                .onErrorResume(UserServiceException.class, Response::monoError)
                .onErrorResume(Exception.class, Response::monoError);
    }

    public Mono<ResponseEntity<String>> activateAccount(String activationCode) {
        return activate.byActivationCodeSentByEmail(activationCode)
                .map(ResponseEntity::ok)
                .onErrorResume(UserServiceException.class, uEx ->
                        Mono.just(ResponseEntity.badRequest()
                                .body(error(uEx.getMessage(), uEx.getErrors()).toString()))
                )
                .onErrorResume(Exception.class, ex ->
                        Mono.just(ResponseEntity.badRequest()
                                .body(error(ex.getMessage()).toString())));

    }

    public Mono<Response> resendActivateCode(String email) {
        return activate.resendActivationCode(email)
                .map(resended -> success("Activation mail successfully resented", resended))
                .onErrorResume(UserServiceException.class, Response::monoError)
                .onErrorResume(Exception.class, Response::monoError);

    }

    public Mono<ResponseEntity<String>> resetPasswordByCode(String resetCode) {
        return resetComponent.resetPasswordByCodeAndReturnNewPassForm(resetCode)
                .map(ResponseEntity::ok)
                .onErrorResume(UserServiceException.class, uEx ->
                        Mono.just(ResponseEntity.badRequest()
                                .body(convertToJsonAsString(error(uEx.getMessage(), uEx.getErrors()))))
                )
                .onErrorResume(Exception.class, ex ->
                        Mono.just(ResponseEntity.badRequest()
                                .body(convertToJsonAsString(error(ex.getMessage())))));
    }

    public Mono<ResponseEntity<String>> setNewPasswordByCode(UserJson userJson) {
        return resetComponent.setNewPasswordByCode(userJson)
                .map(ResponseEntity::ok)
                .onErrorResume(UserServiceException.class, uEx ->
                        Mono.just(ResponseEntity.badRequest().body(convertToJsonAsString(error(uEx.getMessage(), uEx.getErrors()))))
                )
                .onErrorResume(Exception.class, ex ->
                        Mono.just(ResponseEntity.badRequest().body(convertToJsonAsString(error(ex.getMessage())))));
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
