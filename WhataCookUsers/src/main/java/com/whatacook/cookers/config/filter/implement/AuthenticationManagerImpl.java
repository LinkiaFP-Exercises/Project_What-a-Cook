package com.whatacook.cookers.config.filter.implement;

import com.whatacook.cookers.config.filter.AuthenticationManager;
import com.whatacook.cookers.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Implementation of AuthenticationManager.
 * Handles user authentication by setting the authentication context.
 * <p>
 * Annotations:
 * - @Component: Indicates that this class is a Spring component.
 * - @AllArgsConstructor: Generates a constructor with one parameter for each field.
 * <p>
 * Fields:
 * - userService: Service for handling user-related logic.
 * <p>
 * Methods:
 * - setAuthenticated(String userEmailOrId, String token, ServerWebExchange exchange, WebFilterChain chain): Sets the authentication context for the user.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@AllArgsConstructor
@Component
public class AuthenticationManagerImpl implements AuthenticationManager {

    private final UserService userService;

    @Override
    public Mono<Void> setAuthenticated(String userEmailOrId, String token, ServerWebExchange exchange, WebFilterChain chain) {
        return userService.findByUsername(userEmailOrId)
                .map(user -> new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities()))
                .cast(Authentication.class)
                .flatMap(authentication -> chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication)))
                .then();
    }
}
