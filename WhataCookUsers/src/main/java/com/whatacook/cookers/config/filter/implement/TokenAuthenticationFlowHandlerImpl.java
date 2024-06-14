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

import static com.whatacook.cookers.model.responses.Response.error;
import static com.whatacook.cookers.utilities.Util.convertToJsonAsBytes;

/**
 * Implementation of TokenAuthenticationFlowHandler.
 * Handles the token authentication flow by verifying the JWT token and authenticating the user.
 * <p>
 * Annotations:
 * - @Component: Indicates that this class is a Spring component.
 * - @AllArgsConstructor: Generates a constructor with one parameter for each field.
 * <p>
 * Fields:
 * - jwtUtil: Utility class for JWT operations.
 * - authenticationManager: Manager for handling authentication.
 * <p>
 * Methods:
 * - handle(String requestToken, ServerWebExchange exchange, WebFilterChain chain): Handles the token authentication flow.
 * - handleError(Throwable e, ServerWebExchange exchange): Handles errors during the token authentication process.
 * - sendUnauthorizedResponse(ServerWebExchange exchange, String errorMessage): Sends an unauthorized response.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@AllArgsConstructor
@Component
public class TokenAuthenticationFlowHandlerImpl implements TokenAuthenticationFlowHandler {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public Mono<Void> handle(String requestToken, ServerWebExchange exchange, WebFilterChain chain) {
        return Mono.justOrEmpty(requestToken)
                .filter(token -> jwtUtil.hasToken(token) && jwtUtil.isValidToken(token))
                .flatMap(token -> {
                    String tokenWithoutPrefix = jwtUtil.extractPrefix(token);
                    String username = jwtUtil.getUsernameFromToken(tokenWithoutPrefix);
                    return authenticationManager.setAuthenticated(username, tokenWithoutPrefix, exchange, chain);
                })
                .onErrorResume(e -> handleError(e, exchange)); // Handle error globally
    }

    private Mono<Void> handleError(Throwable e, ServerWebExchange exchange) {
        if (!exchange.getResponse().isCommitted()) {
            String errorMessage;
            if (e instanceof ExpiredJwtException)
                errorMessage = "Token expired. Please login again.";
            else if (e instanceof JwtException)
                errorMessage = "Invalid token.";
            else
                errorMessage = e.getMessage();

            return sendUnauthorizedResponse(exchange, errorMessage);
        } else {
            return Mono.empty();
        }
    }

    private Mono<Void> sendUnauthorizedResponse(ServerWebExchange exchange, String errorMessage) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Response errorResponse = error(errorMessage);
        byte[] bytes = convertToJsonAsBytes(errorResponse);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }
}
