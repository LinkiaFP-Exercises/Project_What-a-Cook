package com.whatacook.cookers.config.filter.implement;

import com.whatacook.cookers.config.filter.AuthenticationManager;
import com.whatacook.cookers.config.filter.ResponseErrorHtml;
import com.whatacook.cookers.config.filter.SetNewPasswordFlowHandler;
import com.whatacook.cookers.model.constants.Htmls;
import com.whatacook.cookers.service.ResetService;
import com.whatacook.cookers.utilities.GlobalValues;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@AllArgsConstructor @Component
public class SetNewPasswordFlowHandlerImpl implements SetNewPasswordFlowHandler {

    private final ResetService resetService;
    private final GlobalValues globalValues;
    private final AuthenticationManager authenticationManager;
    private final ResponseErrorHtml responseErrorHtml;

    @SuppressWarnings("ReactorTransformationOnMonoVoid")
    @Override
    public Mono<Void> handle(String keyCodeToSet, ServerWebExchange exchange, WebFilterChain chain) {
        String codeToSet = exchange.getRequest().getQueryParams().getFirst(keyCodeToSet);
        String FAIL_HTML_FOR_RESET = Htmls.FailSetNewPassword.get()
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("EMAIL_WAC", globalValues.getMailToWac());
        return resetService.findByCode(codeToSet)
                .flatMap(resetDto -> authenticationManager.setAuthenticated(resetDto.getId(), null, exchange, chain))
                .switchIfEmpty(Mono.defer(() -> responseErrorHtml.send(exchange, FAIL_HTML_FOR_RESET)));
    }
}
