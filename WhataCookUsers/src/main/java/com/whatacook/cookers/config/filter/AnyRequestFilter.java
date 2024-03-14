package com.whatacook.cookers.config.filter;

import com.whatacook.cookers.config.jwt.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
public class AnyRequestFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final TokenAuthenticationFlowHandler tokenAuthenticationFlowHandler;
    private final ActivationCodeFlowHandler activationCodeFlowHandler;
    private final EmailResendFlowHandler emailResendFlowHandler;
    private final EmailResetPasswordFlowHandler emailResetPasswordFlowHandler;
    private final SetNewPasswordFlowHandler setNewPasswordFlowHandler;

    @SuppressWarnings("NullableProblems")
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        if (exchange.getRequest().getHeaders().containsKey(jwtUtil.getHeader()))
            return tokenAuthenticationFlowHandler.handle(exchange, chain);
        else if (queryParams.containsKey(jwtUtil.getActivation()))
            return activationCodeFlowHandler.handle(jwtUtil.getActivation(), exchange, chain);
        else if (queryParams.containsKey(jwtUtil.getResend()))
            return emailResendFlowHandler.handle(jwtUtil.getResend(), exchange, chain);
        else if (queryParams.containsKey(jwtUtil.getResetCode()))
            return emailResetPasswordFlowHandler.handle(jwtUtil.getResetCode(), exchange, chain);
        else if (queryParams.containsKey(jwtUtil.getCodeToSet()))
            return setNewPasswordFlowHandler.handle(jwtUtil.getCodeToSet(), exchange, chain);
        else
            return chain.filter(exchange);
    }

}
