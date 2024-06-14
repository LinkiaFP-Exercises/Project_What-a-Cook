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

/**
 * Implementation of SetNewPasswordFlowHandler.
 * Handles the set new password flow by verifying the reset code and authenticating the user.
 * <p>
 * Annotations:
 * - @Component: Indicates that this class is a Spring component.
 * - @AllArgsConstructor: Generates a constructor with one parameter for each field.
 * <p>
 * Fields:
 * - resetService: Service for handling reset logic.
 * - globalValues: Utility class for accessing global values.
 * - authenticationManager: Manager for handling authentication.
 * - responseErrorHtml: Service for sending error responses in HTML format.
 * <p>
 * Methods:
 * - handle(String codeToSet, ServerWebExchange exchange, WebFilterChain chain): Handles the set new password flow.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@AllArgsConstructor
@Component
public class SetNewPasswordFlowHandlerImpl implements SetNewPasswordFlowHandler {

    private final ResetService resetService;
    private final GlobalValues globalValues;
    private final AuthenticationManager authenticationManager;
    private final ResponseErrorHtml responseErrorHtml;

    @SuppressWarnings("ReactorTransformationOnMonoVoid")
    @Override
    public Mono<Void> handle(String codeToSet, ServerWebExchange exchange, WebFilterChain chain) {
        String FAIL_HTML_FOR_RESET = Htmls.FailSetNewPassword.get()
                .replace("LOGO_WAC", globalValues.getUrlWacLogoPngSmall())
                .replace("URL_FORGOT_PASS", globalValues.getUrlForgotPassword())
                .replace("EMAIL_WAC", globalValues.getMailToWac());
        return resetService.findByCode(codeToSet)
                .flatMap(resetDto -> authenticationManager.setAuthenticated(resetDto.getId(), null, exchange, chain))
                .switchIfEmpty(Mono.defer(() -> responseErrorHtml.send(exchange,
                        FAIL_HTML_FOR_RESET.replace("errorDescriptionValue", "Code Not Found"))))
                .onErrorResume(throwable -> Mono.defer(() -> responseErrorHtml.send(exchange,
                        FAIL_HTML_FOR_RESET.replace("errorDescriptionValue", throwable.getMessage()))));
    }
}
