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

/**
 * Implementation of ActivationCodeFlowHandler.
 * Handles the activation code flow by verifying the activation code and authenticating the user.
 * <p>
 * Annotations:
 * - @Component: Indicates that this class is a Spring component.
 * - @AllArgsConstructor: Generates a constructor with one parameter for each field.
 * <p>
 * Fields:
 * - activationService: Service for handling activation logic.
 * - globalValues: Utility class for accessing global values.
 * - authenticationManager: Manager for handling authentication.
 * - responseErrorHtml: Service for sending error responses in HTML format.
 * <p>
 * Methods:
 * - handle(String activationCode, ServerWebExchange exchange, WebFilterChain chain): Handles the activation code flow.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@AllArgsConstructor
@Component
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
