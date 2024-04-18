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
    public Mono<Void> handle(String activationCode, ServerWebExchange exchange, WebFilterChain chain) {
        String FAIL_HTML_FOR_ACTIVATION = Htmls.FailActivation.get()
                                            .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                                            .replace("PATH_TO_RESEND", globalValues.getPathToResendActvationMail())
                                            .replace("EMAIL_WAC", globalValues.getMailToWac());
        return activationService.findByCode(activationCode)
                .flatMap(activationDto -> authenticationManager.setAuthenticated(activationDto.getId(), null, exchange, chain))
                .switchIfEmpty(Mono.defer(() -> responseErrorHtml.send(exchange, FAIL_HTML_FOR_ACTIVATION)));
    }


}
