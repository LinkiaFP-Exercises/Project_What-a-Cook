package com.whatacook.cookers.config.filter.implement;

import com.whatacook.cookers.config.filter.AuthenticationManager;
import com.whatacook.cookers.config.filter.TokenAuthenticationFlowHandler;
import com.whatacook.cookers.config.jwt.JwtUtil;
import com.whatacook.cookers.model.responses.Response;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.whatacook.cookers.model.responses.Response.error;
import static com.whatacook.cookers.utilities.Util.convertToJsonAsBytes;

@AllArgsConstructor @Component
public class TokenAuthenticationFlowHandlerImpl implements TokenAuthenticationFlowHandler {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @SuppressWarnings({"ReactorTransformationOnMonoVoid", "DataFlowIssue"})
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, WebFilterChain chain) {
        String requestToken = exchange.getRequest().getHeaders().getFirst(jwtUtil.getHeader());

        return Mono.just(requestToken)
                    .filter(token -> jwtUtil.hasToken(token) && jwtUtil.isValidToken(token))
                    .flatMap(token -> {
                        String tokenWithoutPrefix = jwtUtil.extractPrefix(token);
                        String username = jwtUtil.getUsernameFromToken(tokenWithoutPrefix);
                        return authenticationManager.setAuthenticated(username, tokenWithoutPrefix, exchange, chain);
                    })
                    .onErrorResume(ExpiredJwtException.class, e -> sendUnauthorizedResponse(exchange, "Token expired. Please login again."))
                    .onErrorResume(JwtException.class, e -> sendUnauthorizedResponse(exchange, "Invalid token."))
                .switchIfEmpty(chain.filter(exchange));
    }

    private Mono<Void> sendUnauthorizedResponse(ServerWebExchange exchange, String errorMessage) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now().toString());
        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        errorDetails.put("message", errorMessage);
        errorDetails.put("path", exchange.getRequest().getPath().value());

        Response errorResponse = error(errorMessage, errorDetails);

        byte[] bytes = convertToJsonAsBytes(errorResponse);

        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }

}
