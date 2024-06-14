package com.whatacook.cookers.config.filter;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Interface for managing authentication.
 * Defines a method to set the authentication status based on user email or ID and token.
 * <p>
 * Methods:
 * - setAuthenticated(String userEmailOrId, String token, ServerWebExchange exchange, WebFilterChain chain):
 * Sets the authentication status using the provided user email or ID, token, exchange, and filter chain.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
public interface AuthenticationManager {
    Mono<Void> setAuthenticated(String userEmailOrId, String token, ServerWebExchange exchange, WebFilterChain chain);
}
