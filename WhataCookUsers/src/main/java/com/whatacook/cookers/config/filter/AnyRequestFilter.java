package com.whatacook.cookers.config.filter;

import com.whatacook.cookers.config.jwt.JwtUtil;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
public class AnyRequestFilter implements WebFilter {

    private final Map<String, RequestHandler> handlers;

    public AnyRequestFilter(JwtUtil jwtUtil, TokenAuthenticationFlowHandler tokenAuthenticationFlowHandler,
                            ActivationCodeFlowHandler activationCodeFlowHandler,
                            EmailResendFlowHandler emailResendFlowHandler,
                            EmailResetPasswordFlowHandler emailResetPasswordFlowHandler,
                            SetNewPasswordFlowHandler setNewPasswordFlowHandler) {
        handlers = new HashMap<>();
        handlers.put(jwtUtil.getHeader(), tokenAuthenticationFlowHandler::handle);
        handlers.put(jwtUtil.getActivation(), (exchange, chain) -> activationCodeFlowHandler.handle(getKeyFromRequest(exchange, jwtUtil.getActivation()), exchange, chain));
        handlers.put(jwtUtil.getResend(), (exchange, chain) -> emailResendFlowHandler.handle(getKeyFromRequest(exchange, jwtUtil.getResend()), exchange, chain));
        handlers.put(jwtUtil.getResetCode(), (exchange, chain) -> emailResetPasswordFlowHandler.handle(getKeyFromRequest(exchange, jwtUtil.getResetCode()), exchange, chain));
        handlers.put(jwtUtil.getCodeToSet(), (exchange, chain) -> setNewPasswordFlowHandler.handle(getKeyFromRequest(exchange, jwtUtil.getCodeToSet()), exchange, chain));
    }

    @SuppressWarnings("NullableProblems") @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
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

    private String getKeyFromRequest(ServerWebExchange exchange, String key) {
        return exchange.getRequest().getQueryParams().getFirst(key);
    }

    @FunctionalInterface
    public interface RequestHandler {
        Mono<Void> handle(ServerWebExchange exchange, WebFilterChain chain);
    }

}
