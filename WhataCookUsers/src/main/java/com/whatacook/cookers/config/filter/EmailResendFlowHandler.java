package com.whatacook.cookers.config.filter;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Interface for handling the flow of resending an email.
 * Defines a method to handle the process based on a provided email to resend.
 * <p>
 * Methods:
 * - handle(String emailToResend, ServerWebExchange exchange, WebFilterChain chain):
 * Handles the process of resending an email using the provided email to resend, exchange, and filter chain.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
public interface EmailResendFlowHandler {
    Mono<Void> handle(String emailToResend, ServerWebExchange exchange, WebFilterChain chain);
}
