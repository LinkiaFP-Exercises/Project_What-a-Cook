package com.whatacook.cookers.service;

import com.whatacook.cookers.config.jwt.JwtUtil;
import com.whatacook.cookers.model.auth.AuthRequestDto;
import com.whatacook.cookers.model.responses.Response;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import reactor.core.publisher.Mono;

import static com.whatacook.cookers.model.responses.Response.error;
import static com.whatacook.cookers.model.responses.Response.success;

@AllArgsConstructor
@Service
@Validated
public class AuthService {

    private final ReactiveAuthenticationManager reactiveAuthenticationManager;
    private final JwtUtil jwtUtil;

    public Mono<ResponseEntity<Response>> authenticationByLogin(@RequestBody AuthRequestDto authRequestDto) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                authRequestDto.getUsername(), authRequestDto.getPassword());

        return reactiveAuthenticationManager.authenticate(authentication)
                .flatMap(auth -> Mono.just(ResponseEntity.ok(success("TOKEN",
                        jwtUtil.generateToken(authRequestDto)))))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(error("Authentication failed: " + e.getMessage()))));
    }

}
