package com.whatacook.cookers.config.filter;

import com.whatacook.cookers.config.jwt.JwtUtil;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
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

        handlers.put(jwtUtil.getHeader(), (exchange, chain, keys) -> tokenAuthenticationFlowHandler.handle(exchange, chain));
        handlers.put(jwtUtil.getActivation(), (exchange, chain, keys) -> activationCodeFlowHandler.handle(keys[0], exchange, chain));
        handlers.put(jwtUtil.getResend(), (exchange, chain, keys) -> emailResendFlowHandler.handle(keys[0], exchange, chain));
        handlers.put(jwtUtil.getResetCode(), (exchange, chain, keys) -> emailResetPasswordFlowHandler.handle(keys[0], exchange, chain));
        handlers.put(jwtUtil.getCodeToSet(), (exchange, chain, keys) -> setNewPasswordFlowHandler.handle(keys[0], exchange, chain));
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {

        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();

        for (Map.Entry<String, RequestHandler> entry : handlers.entrySet()) {
            if (exchange.getRequest().getHeaders().containsKey(entry.getKey())
                    || queryParams.containsKey(entry.getKey())) {

                String[] key = queryParams.containsKey(entry.getKey())
                        ? new String[]{queryParams.getFirst(entry.getKey())} : new String[]{};
                return entry.getValue().handle(exchange, chain, key);
            }
        }

        return chain.filter(exchange);
    }

    @FunctionalInterface
    public interface RequestHandler {
        Mono<Void> handle(ServerWebExchange exchange, WebFilterChain chain, String... keys);
    }

}
