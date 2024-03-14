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

@AllArgsConstructor @Component
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
