package com.whatacook.cookers.config.filter.implement;

import com.whatacook.cookers.config.filter.AuthenticationManager;
import com.whatacook.cookers.config.filter.EmailResetPasswordFlowHandler;
import com.whatacook.cookers.config.filter.ResponseErrorHtml;
import com.whatacook.cookers.service.ResetService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@AllArgsConstructor @Component
public class EmailResetPasswordFlowHandlerImpl implements EmailResetPasswordFlowHandler {

    private final ResetService resetService;
    private final AuthenticationManager authenticationManager;
    private final ResponseErrorHtml responseErrorHtml;
    @SuppressWarnings("ReactorTransformationOnMonoVoid")
    @Override
    public Mono<Void> handle(String keyResetCode, ServerWebExchange exchange, WebFilterChain chain) {
        String resetCode = exchange.getRequest().getQueryParams().getFirst(keyResetCode);
        return resetService.findByCode(resetCode)
                .flatMap(resetDto -> authenticationManager.setAuthenticated(resetDto.getId(), null, exchange, chain))
                .switchIfEmpty(Mono.defer(() -> responseErrorHtml.send(exchange, "<h1>Fallo en el filter!</h1>")));
    }
}
