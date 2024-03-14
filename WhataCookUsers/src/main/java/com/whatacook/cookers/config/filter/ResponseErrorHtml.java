package com.whatacook.cookers.config.filter;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface ResponseErrorHtml {
    Mono<Void> send(ServerWebExchange exchange, String htmlContent);
}
