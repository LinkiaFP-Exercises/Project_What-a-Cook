package com.whatacook.cookers.config.filter;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public interface AuthenticationManager {
    Mono<Void> setAuthenticated(String userEmailOrId, String token, ServerWebExchange exchange, WebFilterChain chain);
}

