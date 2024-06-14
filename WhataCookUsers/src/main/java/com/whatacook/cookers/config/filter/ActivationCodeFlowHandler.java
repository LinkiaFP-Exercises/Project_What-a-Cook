package com.whatacook.cookers.config.filter;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Interface for handling the flow of activating an account using an activation code.
 * Defines a method to handle the process based on a provided activation code.
 * <p>
 * Methods:
 * - handle(String activationCode, ServerWebExchange exchange, WebFilterChain chain):
 * Handles the activation process using the provided activation code, exchange, and filter chain.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
public interface ActivationCodeFlowHandler {
    Mono<Void> handle(String activationCode, ServerWebExchange exchange, WebFilterChain chain);
}
