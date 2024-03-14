package com.whatacook.cookers.config.filter.implement;

import com.whatacook.cookers.config.filter.ResponseErrorHtml;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Component
public class ResponseErrorHtmlImpl implements ResponseErrorHtml {
    @Override
    public Mono<Void> send(ServerWebExchange exchange, String htmlContent) {
        if (!exchange.getResponse().isCommitted()) {
            exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
            exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_HTML);
            DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(htmlContent.getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(dataBuffer));
        } else {
            return Mono.empty();
        }
    }
}
