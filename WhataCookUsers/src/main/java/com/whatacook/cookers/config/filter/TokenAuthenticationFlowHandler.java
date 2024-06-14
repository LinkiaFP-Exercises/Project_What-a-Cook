package com.whatacook.cookers.config.filter;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Interface for handling token authentication flow.
 * Defines a method to handle the authentication process based on a request token.
 * <p>
 * Methods:
 * - handle(String requestToken, ServerWebExchange exchange, WebFilterChain chain):
 * Handles the authentication process using the provided request token, exchange, and filter chain.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
public interface TokenAuthenticationFlowHandler {
    Mono<Void> handle(String requestToken, ServerWebExchange exchange, WebFilterChain chain);
}
