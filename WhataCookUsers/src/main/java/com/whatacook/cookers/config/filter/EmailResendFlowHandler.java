package com.whatacook.cookers.config.filter;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public interface EmailResendFlowHandler {
    Mono<Void> handle(String emailToResend, ServerWebExchange exchange, WebFilterChain chain);
}
