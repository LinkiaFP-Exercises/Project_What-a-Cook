package com.whatacook.cookers.config.filter;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public interface ActivationCodeFlowHandler {
    Mono<Void> handle(String activationCode, ServerWebExchange exchange, WebFilterChain chain);
}
