package com.whatacook.cookers.config.filter;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public interface EmailResetPasswordFlowHandler {
    Mono<Void> handle(String resetCode, ServerWebExchange exchange, WebFilterChain chain);
}
