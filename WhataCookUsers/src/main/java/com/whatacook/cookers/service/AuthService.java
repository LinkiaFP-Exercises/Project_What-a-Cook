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

@AllArgsConstructor
@Service
@Validated
public class AuthService {

    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDao DAO;
    private final SaveComponent create;
    private final EmailService emailService;

    public Mono<Response> registerNewUser(UserJustToSave userJson) {
        return create.saveUser(userJson)
                .map(saved -> success("User successfully created", saved))
                .onErrorResume(UserServiceException.class, Response::monoError)
                .onErrorResume(Exception.class, Response::monoError);
    }

    public Mono<ResponseEntity<Response>> authenticationByLogin(@Valid AuthRequestDto authRequestDto) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authRequestDto.getUsername(), authRequestDto.getPassword());

        return reactiveAuthenticationManager.authenticate(authentication)
                .flatMap(auth -> Mono.just(ResponseEntity.ok(success("TOKEN",
                        jwtUtil.generateToken(authRequestDto)))))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(error("Authentication failed: " + e.getMessage()))));
    }

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
