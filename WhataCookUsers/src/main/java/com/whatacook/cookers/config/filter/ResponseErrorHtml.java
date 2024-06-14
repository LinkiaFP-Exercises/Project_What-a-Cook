package com.whatacook.cookers.config.filter;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Interface for sending error responses in HTML format.
 * Defines a method to send an error response with HTML content.
 * <p>
 * Methods:
 * - send(ServerWebExchange exchange, String htmlContent):
 * Sends an error response with the provided HTML content using the given exchange.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
public interface ResponseErrorHtml {
    Mono<Void> send(ServerWebExchange exchange, String htmlContent);
}
