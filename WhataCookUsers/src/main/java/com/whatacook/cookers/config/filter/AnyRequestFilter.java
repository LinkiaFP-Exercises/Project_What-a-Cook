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

@Component
public class AnyRequestFilter implements WebFilter {

    private final Map<String, RequestHandler> handlers;

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

    private RequestHandler createHandler(TriFunction<String, ServerWebExchange, WebFilterChain, Mono<Void>> handlerFunction, String jwtKey) {
        return (exchange, chain) -> {
            String paramValue = getHeaderOrParamValue(exchange, jwtKey);
            return handlerFunction.apply(paramValue, exchange, chain);
        };
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Mono<Void> filter(@NotNull ServerWebExchange exchange, @NotNull WebFilterChain chain) {
        return handlers.entrySet().stream()
                .filter(entry -> requestContainsKey(exchange, entry.getKey()))
                .findFirst()
                .map(entry -> entry.getValue().handle(exchange, chain))
                .orElseGet(() -> chain.filter(exchange));
    }

    private boolean requestContainsKey(ServerWebExchange exchange, String key) {
        return exchange.getRequest().getHeaders().containsKey(key) ||
                exchange.getRequest().getQueryParams().containsKey(key);
    }

    private String getHeaderOrParamValue(ServerWebExchange exchange, String key) {
        return Optional.ofNullable(exchange.getRequest().getHeaders().getFirst(key))
                .orElse(exchange.getRequest().getQueryParams().getFirst(key));
    }

    @FunctionalInterface
    public interface RequestHandler {
        Mono<Void> handle(ServerWebExchange exchange, WebFilterChain chain);
    }

    @FunctionalInterface
    public interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
    }

}
