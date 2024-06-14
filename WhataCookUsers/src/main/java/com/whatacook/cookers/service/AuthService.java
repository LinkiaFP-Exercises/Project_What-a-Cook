package com.whatacook.cookers.service;

import com.whatacook.cookers.config.jwt.JwtUtil;
import com.whatacook.cookers.model.auth.AuthRequestDto;
import com.whatacook.cookers.model.exceptions.UserServiceException;
import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.model.users.UserJson;
import com.whatacook.cookers.model.users.UserJustToSave;
import com.whatacook.cookers.service.components.SaveComponent;
import com.whatacook.cookers.service.contracts.UserDao;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;

import static com.whatacook.cookers.model.responses.Response.error;
import static com.whatacook.cookers.model.responses.Response.success;

/**
 * Service class for handling authentication and user registration.
 * <p>
 * Annotations:
 * - @AllArgsConstructor: Generates a constructor with 1 parameter for each field.
 * - @Service: Indicates that this class is a Spring service.
 * - @Validated: Enables validation for method parameters in this class.
 * <p>
 * Fields:
 * - reactiveAuthenticationManager: The authentication manager for handling authentication requests.
 * - jwtUtil: Utility class for generating and validating JWT tokens.
 * - DAO: The user data access object for interacting with the database.
 * - create: Component for saving new users.
 * - emailService: Service for handling email-related operations.
 * <p>
 * Methods:
 * - registerNewUser(UserJustToSave userJson): Registers a new user.
 * - authenticationByLogin(@Valid AuthRequestDto authRequestDto): Authenticates a user by login.
 * - sendEmailCodeToResetPassword(@Valid UserJson userJson): Sends an email with a reset code for password reset.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 * @see ReactiveAuthenticationManager
 * @see JwtUtil
 * @see UserDao
 * @see SaveComponent
 * @see EmailService
 * @see AuthRequestDto
 * @see UserJson
 * @see UserJustToSave
 * @see Mono
 * @see ResponseEntity
 * @see Response
 * @see HttpStatus
 * @see Authentication
 * @see UsernamePasswordAuthenticationToken
 * @see Validated
 * @see Valid
 * @see AllArgsConstructor
 * @see Service
 */
@AllArgsConstructor
@Service
@Validated
public class AuthService {

    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDao DAO;
    private final SaveComponent create;
    private final EmailService emailService;

    /**
     * Registers a new user.
     *
     * @param userJson the user details to save
     * @return a Mono containing the response with the saved user details
     */
    public Mono<Response> registerNewUser(UserJustToSave userJson) {
        return create.saveUser(userJson)
                .map(saved -> success("User successfully created", saved))
                .onErrorResume(UserServiceException.class, Response::monoError)
                .onErrorResume(Exception.class, Response::monoError);
    }

    /**
     * Authenticates a user by login.
     *
     * @param authRequestDto the authentication request details
     * @return a Mono containing the response entity with the authentication result
     */
    public Mono<ResponseEntity<Response>> authenticationByLogin(@Valid AuthRequestDto authRequestDto) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authRequestDto.getUsername(), authRequestDto.getPassword());

        return reactiveAuthenticationManager.authenticate(authentication)
                .flatMap(auth -> Mono.just(ResponseEntity.ok(success("TOKEN",
                        jwtUtil.generateToken(authRequestDto)))))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(error("Authentication failed: " + e.getMessage()))));
    }

    /**
     * Sends an email with a reset code for password reset.
     *
     * @param userJson the user details for password reset
     * @return a Mono containing the response entity with the result of the email sending
     */
    public Mono<ResponseEntity<Response>> sendEmailCodeToResetPassword(@Valid UserJson userJson) {
        return DAO.findByEmail(userJson.getEmail())
                .flatMap(userDTO -> {
                    if (userDTO.getBirthdate().equals(userJson.getBirthdate())) {
                        return emailService.createResetCodeAndSendEmail(userDTO)
                                .map(user -> ResponseEntity.ok(success("Email sent with reset code", user)));
                    } else {
                        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(error("Incorrect information")));
                    }
                })
                .switchIfEmpty(Mono.just(ResponseEntity.ok(error("Unregistered email"))));
    }

}
