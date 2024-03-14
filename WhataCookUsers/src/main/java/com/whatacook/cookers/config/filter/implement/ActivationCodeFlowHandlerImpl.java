package com.whatacook.cookers.config.filter.implement;

import com.whatacook.cookers.config.filter.ActivationCodeFlowHandler;
import com.whatacook.cookers.config.filter.AuthenticationManager;
import com.whatacook.cookers.config.filter.ResponseErrorHtml;
import com.whatacook.cookers.model.constants.Htmls;
import com.whatacook.cookers.service.ActivationService;
import com.whatacook.cookers.utilities.GlobalValues;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;


@AllArgsConstructor @Component
public class ActivationCodeFlowHandlerImpl implements ActivationCodeFlowHandler {

    private final ActivationService activationService;
    private final GlobalValues globalValues;
    private final AuthenticationManager authenticationManager;
    private final ResponseErrorHtml responseErrorHtml;

    @SuppressWarnings("ReactorTransformationOnMonoVoid")
    @Override
    public Mono<Void> handle(String keyActivationCode, ServerWebExchange exchange, WebFilterChain chain) {
        String activationCode = exchange.getRequest().getQueryParams().getFirst(keyActivationCode);
        String FAIL_HTML_FOR_ACTIVATION = String.format(Htmls.FailActivation.get(), globalValues.getUrlWacLogoPngSmall(),
                globalValues.getPathToResendActvationMail(), globalValues.getMailToWac());
        return activationService.findByCode(activationCode)
                .flatMap(activationDto -> authenticationManager.setAuthenticated(activationDto.getId(), null, exchange, chain))
                .switchIfEmpty(Mono.defer(() -> responseErrorHtml.send(exchange, FAIL_HTML_FOR_ACTIVATION)));
    }


}
