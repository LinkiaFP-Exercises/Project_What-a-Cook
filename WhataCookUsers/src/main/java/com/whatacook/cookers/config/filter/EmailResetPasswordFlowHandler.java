package com.whatacook.cookers.config.filter;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Interface for handling the flow of resetting an email password.
 * Defines a method to handle the process based on a provided reset code.
 * <p>
 * Methods:
 * - handle(String resetCode, ServerWebExchange exchange, WebFilterChain chain):
 * Handles the process of resetting an email password using the provided reset code, exchange, and filter chain.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
public interface EmailResetPasswordFlowHandler {
    Mono<Void> handle(String resetCode, ServerWebExchange exchange, WebFilterChain chain);
}
