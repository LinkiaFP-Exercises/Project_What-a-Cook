package com.whatacook.cookers.config.filter;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public interface TokenAuthenticationFlowHandler {
    Mono<Void> handle(String requestToken, ServerWebExchange exchange, WebFilterChain chain);

}
