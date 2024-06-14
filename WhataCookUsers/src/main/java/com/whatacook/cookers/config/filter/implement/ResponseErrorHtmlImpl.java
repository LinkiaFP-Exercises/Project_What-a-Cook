package com.whatacook.cookers.config.filter.implement;

import com.whatacook.cookers.config.filter.ResponseErrorHtml;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * Implementation of ResponseErrorHtml.
 * Sends error responses in HTML format.
 * <p>
 * Annotations:
 * - @Component: Indicates that this class is a Spring component.
 * <p>
 * Methods:
 * - send(ServerWebExchange exchange, String htmlContent): Sends an HTML error response.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@Component
public class ResponseErrorHtmlImpl implements ResponseErrorHtml {
    @Override
    public Mono<Void> send(ServerWebExchange exchange, String htmlContent) {
        if (!exchange.getResponse().isCommitted()) {
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_HTML);
            DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(htmlContent.getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(dataBuffer));
        } else {
            return Mono.empty();
        }
    }
}
