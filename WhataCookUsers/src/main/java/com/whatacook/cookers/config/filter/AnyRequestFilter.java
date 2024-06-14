package com.whatacook.cookers.config.filter;

import com.whatacook.cookers.config.jwt.JwtUtil;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;

/**
 * Web filter that handles various request flows based on the presence of specific headers or query parameters.
 * Utilizes different handlers for JWT authentication, activation codes, email resending, password resetting, and setting new passwords.
 * <p>
 * Annotations:
 * - @Component: Indicates that this class is a Spring component.
 * <p>
 * Fields:
 * - handlers: A map of request handlers associated with specific JWT keys.
 * <p>
 * Methods:
 * - AnyRequestFilter(JwtUtil jwtUtil, TokenAuthenticationFlowHandler tokenAuthenticationFlowHandler, ActivationCodeFlowHandler activationCodeFlowHandler,
 * EmailResendFlowHandler emailResendFlowHandler, EmailResetPasswordFlowHandler emailResetPasswordFlowHandler, SetNewPasswordFlowHandler setNewPasswordFlowHandler):
 * Constructor that initializes the handlers map with appropriate request handlers.
 * - createHandler(TriFunction<String, ServerWebExchange, WebFilterChain, Mono<Void>> handlerFunction, String jwtKey): Creates a request handler.
 * - filter(ServerWebExchange exchange, WebFilterChain chain): Filters the web exchange and delegates to the appropriate handler.
 * - requestContainsKey(ServerWebExchange exchange, String key): Checks if the request contains the specified key.
 * - getHeaderOrParamValue(ServerWebExchange exchange, String key): Retrieves the value of the specified key from the request headers or query parameters.
 * <p>
 * Functional Interfaces:
 * - RequestHandler: Functional interface for handling requests.
 * - TriFunction: Functional interface that takes three arguments and produces a result.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@Component
public class AnyRequestFilter implements WebFilter {

    private final Map<String, RequestHandler> handlers;

    /**
     * Constructor that initializes the handlers map with appropriate request handlers.
     *
     * @param jwtUtil                        The utility class for JWT operations.
     * @param tokenAuthenticationFlowHandler The handler for token authentication flow.
     * @param activationCodeFlowHandler      The handler for activation code flow.
     * @param emailResendFlowHandler         The handler for email resend flow.
     * @param emailResetPasswordFlowHandler  The handler for email reset password flow.
     * @param setNewPasswordFlowHandler      The handler for setting new password flow.
     */
    public AnyRequestFilter(JwtUtil jwtUtil, TokenAuthenticationFlowHandler tokenAuthenticationFlowHandler,
                            ActivationCodeFlowHandler activationCodeFlowHandler,
                            EmailResendFlowHandler emailResendFlowHandler,
                            EmailResetPasswordFlowHandler emailResetPasswordFlowHandler,
                            SetNewPasswordFlowHandler setNewPasswordFlowHandler) {
        handlers = Map.of(
                jwtUtil.getHeader(), createHandler(tokenAuthenticationFlowHandler::handle, jwtUtil.getHeader()),
                jwtUtil.getActivation(), createHandler(activationCodeFlowHandler::handle, jwtUtil.getActivation()),
                jwtUtil.getResend(), createHandler(emailResendFlowHandler::handle, jwtUtil.getResend()),
                jwtUtil.getResetCode(), createHandler(emailResetPasswordFlowHandler::handle, jwtUtil.getResetCode()),
                jwtUtil.getCodeToSet(), createHandler(setNewPasswordFlowHandler::handle, jwtUtil.getCodeToSet())
        );
    }

    /**
     * Creates a request handler that processes requests based on the specified handler function and JWT key.
     *
     * @param handlerFunction The function to handle the request.
     * @param jwtKey          The JWT key used to identify the request.
     * @return A RequestHandler that processes the request.
     */
    private RequestHandler createHandler(TriFunction<String, ServerWebExchange, WebFilterChain, Mono<Void>> handlerFunction, String jwtKey) {
        return (exchange, chain) -> {
            String paramValue = getHeaderOrParamValue(exchange, jwtKey);
            return handlerFunction.apply(paramValue, exchange, chain);
        };
    }

    /**
     * Filters the web exchange and delegates to the appropriate handler based on the presence of specific headers or query parameters.
     *
     * @param exchange The current server web exchange.
     * @param chain    The web filter chain.
     * @return A Mono<Void> that indicates when request processing is complete.
     */
    @SuppressWarnings("NullableProblems")
    @Override
    public Mono<Void> filter(@NotNull ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        return handlers.entrySet().stream()
                .filter(entry -> requestContainsKey(exchange, entry.getKey()))
                .findFirst()
                .map(entry -> entry.getValue().handle(exchange, chain))
                .orElseGet(() -> chain.filter(exchange));
    }

    /**
     * Checks if the request contains the specified key in headers or query parameters.
     *
     * @param exchange The current server web exchange.
     * @param key      The key to check in the request.
     * @return True if the request contains the specified key, false otherwise.
     */
    private boolean requestContainsKey(ServerWebExchange exchange, String key) {
        return exchange.getRequest().getHeaders().containsKey(key) ||
                exchange.getRequest().getQueryParams().containsKey(key);
    }

    /**
     * Retrieves the value of the specified key from the request headers or query parameters.
     *
     * @param exchange The current server web exchange.
     * @param key      The key to retrieve the value for.
     * @return The value of the specified key, or null if not found.
     */
    private String getHeaderOrParamValue(ServerWebExchange exchange, String key) {
        return Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(key))
                .orElse(exchange.getRequest().getQueryParams().getFirst(key));
    }

    /**
     * Functional interface for handling requests.
     */
    @FunctionalInterface
    public interface RequestHandler {
        Mono<Void> handle(ServerWebExchange exchange, WebFilterChain chain);
    }

    /**
     * Functional interface that takes three arguments and produces a result.
     *
     * @param <T> The type of the first argument to the function.
     * @param <U> The type of the second argument to the function.
     * @param <V> The type of the third argument to the function.
     * @param <R> The type of the result of the function.
     */
    @FunctionalInterface
    public interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }

}
