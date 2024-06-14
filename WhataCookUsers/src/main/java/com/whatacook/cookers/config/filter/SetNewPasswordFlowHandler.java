package com.whatacook.cookers.config.filter;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Interface for handling the flow of setting a new password.
 * Defines a method to handle the process based on a provided code.
 * <p>
 * Methods:
 * - handle(String codeToSet, ServerWebExchange exchange, WebFilterChain chain):
 * Handles the process of setting a new password using the provided code, exchange, and filter chain.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
public interface SetNewPasswordFlowHandler {
    Mono<Void> handle(String codeToSet, ServerWebExchange exchange, WebFilterChain chain);
}
