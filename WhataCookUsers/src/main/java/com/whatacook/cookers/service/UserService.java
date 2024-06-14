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

/**
 * Service class for managing user operations such as authentication, registration, and user information updates.
 * <p>
 * Annotations:
 * - @AllArgsConstructor: Generates a constructor with 1 parameter for each field.
 * - @Service: Indicates that this class is a Spring service.
 * <p>
 * Fields:
 * - read: Component for reading user information.
 * - update: Component for updating user information.
 * - delete: Component for deleting user information.
 * - login: Component for login operations.
 * - activate: Component for account activation operations.
 * - resetComponent: Component for password reset operations.
 * <p>
 * Methods:
 * - existsByEmail(UserJson userJson): Checks if a user exists by email.
 * - activateAccount(String activationCode): Activates a user account using an activation code.
 * - resendActivateCode(String email): Resends the activation code to a user's email.
 * - resetPasswordByCode(String resetCode): Resets a user's password using a reset code.
 * - setNewPasswordByCode(UserJson userJson): Sets a new password for a user using a reset code.
 * - readOne(UserJson userJson): Reads a user's information.
 * - updateOne(UserJson userJson): Updates a user's information.
 * - deleteOne(UserJson userJson): Deletes a user's information.
 * - findByUsername(String userEmailOrId): Finds a user by username (email or ID).
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see FindComponent
 * @see UpdateComponent
 * @see DeleteComponent
 * @see LoginComponent
 * @see ActivateComponent
 * @see ResetComponent
 * @see UserAccessContractModel
 * @see Response
 * @see UserJson
 * @see Mono
 * @see AllArgsConstructor
 * @see Service
 */
@AllArgsConstructor
@Service
public final class UserService implements UserAccessContractModel {
    private final FindComponent read;
    private final UpdateComponent update;
    private final DeleteComponent delete;
    private final LoginComponent login;
    private final ActivateComponent activate;
    private final ResetComponent resetComponent;

    /**
     * Checks if a user exists by email.
     *
     * @param userJson the user details
     * @return a response indicating whether the user exists
     */
    @Override
    public Mono<Response> existsByEmail(UserJson userJson) {
        return read.checkIfExistsByEmail(userJson)
                .map(alreadyExists -> alreadyExists
                        ? success("User already exists", true)
                        : success("User does not exist yet", false))
                .onErrorResume(UserServiceException.class, Response::monoError)
                .onErrorResume(Exception.class, Response::monoError);
    }

    /**
     * Activates a user account using an activation code.
     *
     * @param activationCode the activation code
     * @return a response entity indicating the result of the activation
     */
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

    /**
     * Resends the activation code to a user's email.
     *
     * @param email the user's email
     * @return a response indicating whether the activation code was resent successfully
     */
    public Mono<Response> resendActivateCode(String email) {
        return activate.resendActivationCode(email)
                .map(resended -> success("Activation mail successfully resented", resended))
                .onErrorResume(UserServiceException.class, Response::monoError)
                .onErrorResume(Exception.class, Response::monoError);
    }

    /**
     * Resets a user's password using a reset code.
     *
     * @param resetCode the reset code
     * @return a response entity indicating the result of the password reset
     */
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

    /**
     * Sets a new password for a user using a reset code.
     *
     * @param userJson the user details
     * @return a response entity indicating the result of setting the new password
     */
    public Mono<ResponseEntity<String>> setNewPasswordByCode(UserJson userJson) {
        return resetComponent.setNewPasswordByCode(userJson)
                .map(ResponseEntity::ok)
                .onErrorResume(UserServiceException.class, uEx ->
                        Mono.just(ResponseEntity.badRequest().body(convertToJsonAsString(error(uEx.getMessage(), uEx.getErrors()))))
                )
                .onErrorResume(Exception.class, ex ->
                        Mono.just(ResponseEntity.badRequest().body(convertToJsonAsString(error(ex.getMessage())))));
    }

    /**
     * Reads a user's information.
     *
     * @param userJson the user details
     * @return a response containing the user's information
     */
    @Override
    public Mono<Response> readOne(UserJson userJson) {
        return read.findUserByEmail(userJson)
                .map(found ->
                        success("User successfully read", found))
                .onErrorResume(UserServiceException.class, Response::monoError)
                .onErrorResume(Exception.class, Response::monoError);
    }

    /**
     * Updates a user's information.
     *
     * @param userJson the user details
     * @return a response indicating the result of the update
     */
    @Override
    public Mono<Response> updateOne(UserJson userJson) {
        return update.updateUser(userJson)
                .map(updated ->
                        success("User successfully UPDATED", updated))
                .onErrorResume(UserServiceException.class, Response::monoError)
                .onErrorResume(Exception.class, Response::monoError);
    }

    /**
     * Deletes a user's information.
     *
     * @param userJson the user details
     * @return a response indicating the result of the deletion
     */
    @Override
    public Mono<Response> deleteOne(UserJson userJson) {
        return delete.proceedIfApplicable(userJson)
                .onErrorResume(UserServiceException.class, Response::monoError)
                .onErrorResume(Exception.class, Response::monoError);
    }

    /**
     * Finds a user by username (email or ID).
     *
     * @param userEmailOrId the user's email or ID
     * @return the user details
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public Mono<UserDetails> findByUsername(String userEmailOrId) throws UsernameNotFoundException {
        try {
            return login.validSpringUserToLogin(userEmailOrId);
        } catch (Exception e) {
            throw new UsernameNotFoundException(userEmailOrId);
        }
    }

}
