package com.whatacook.cookers.config.jwt;

import com.whatacook.cookers.model.constants.Htmls;
import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.service.ActivationService;
import com.whatacook.cookers.service.ResetService;
import com.whatacook.cookers.service.UserService;
import com.whatacook.cookers.service.contracts.UserDao;
import com.whatacook.cookers.utilities.GlobalValues;
import com.whatacook.cookers.utilities.Util;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.AllArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.whatacook.cookers.model.responses.Response.error;
import static com.whatacook.cookers.utilities.Util.convertToJsonAsBytes;

@AllArgsConstructor
@SuppressWarnings("ReactorTransformationOnMonoVoid")
@Component
public class JwtRequestFilter implements WebFilter {

    private final ActivationService activationService;
    private final ResetService resetService;
    private final UserService userService;
    private final GlobalValues globalValues;
    private final JwtUtil jwtUtil;
    private final UserDao DAO;

    @SuppressWarnings("NullableProblems")
    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {

        if (exchange.getRequest().getHeaders().containsKey(jwtUtil.getHeader())){
            return handleTokenAuthenticationFlow(exchange, chain);
        }
        else if (exchange.getRequest().getQueryParams().containsKey(jwtUtil.getActivation())) {
            return handleActivationCodeFlow(jwtUtil.getActivation(), exchange, chain);
        }
        else if (exchange.getRequest().getQueryParams().containsKey(jwtUtil.getResend())) {
            return handleEmailResendFlow(jwtUtil.getResend(), exchange, chain);
        }
        else if (exchange.getRequest().getQueryParams().containsKey(jwtUtil.getResetCode())) {
            return handleEmailResetPasswordFlow(jwtUtil.getResetCode(), exchange, chain);
        }
        else if (exchange.getRequest().getQueryParams().containsKey(jwtUtil.getCodeToSet())) {
            return handleSetNewPasswordFlow(jwtUtil.getCodeToSet(), exchange, chain);
        }
        else {
            return chain.filter(exchange);
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private Mono<Void> handleTokenAuthenticationFlow(ServerWebExchange exchange, WebFilterChain chain) {
        String requestToken = exchange.getRequest().getHeaders().getFirst(jwtUtil.getHeader());

        return Mono.just(requestToken)
                .filter(token -> jwtUtil.hasToken(token) && jwtUtil.isValidToken(token))
                .flatMap(token -> {
                    String tokenWithoutPrefix = jwtUtil.extractPrefix(token);
                    String username = jwtUtil.getUsernameFromToken(tokenWithoutPrefix);
                    return setAuthenticated(username, tokenWithoutPrefix, exchange, chain);
                })
                .onErrorResume(ExpiredJwtException.class, e -> sendUNauthorizedResponse(exchange, "Token expired. Please login again."))
                .onErrorResume(JwtException.class, e -> sendUNauthorizedResponse(exchange, "Invalid token."))
                .switchIfEmpty(chain.filter(exchange));
    }

    private Mono<Void> sendUNauthorizedResponse(ServerWebExchange exchange, String errorMessage) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now().toString());
        errorDetails.put("status", status.value());
        errorDetails.put("error", status.getReasonPhrase());
        errorDetails.put("message", errorMessage);
        errorDetails.put("path", exchange.getRequest().getPath().value());

        Response errorResponse = error(errorMessage, errorDetails);

        byte[] bytes = convertToJsonAsBytes(errorResponse);

        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer));
    }


    private Mono<Void> handleActivationCodeFlow(String keyActivationCode, ServerWebExchange exchange, WebFilterChain chain) {
        String activationCode = exchange.getRequest().getQueryParams().getFirst(keyActivationCode);
        String FAIL_HTML_FOR_ACTIVATION = String.format(Htmls.FailActivation.get(), globalValues.getUrlWacLogoPngSmall(),
                                            globalValues.getPathToResendActvationMail(), globalValues.getMailToWac());
        return activationService.findByCode(activationCode)
                .flatMap(activationDto -> setAuthenticated(activationDto.getId(), null, exchange, chain))
                    .switchIfEmpty(Mono.defer(() -> respondWithHtml(exchange, FAIL_HTML_FOR_ACTIVATION)));
    }

    private Mono<Void> handleEmailResendFlow(String keyEmailToResend, ServerWebExchange exchange, WebFilterChain chain) {
        String emailToResend = exchange.getRequest().getQueryParams().getFirst(keyEmailToResend);

        return Mono.just(Objects.requireNonNull(emailToResend)).filter(Util::isValidEmail).flatMap(DAO::findByEmail)
                .flatMap(userDTO -> activationService.findById(userDTO.get_id()))
                        .flatMap(activationDto -> setAuthenticated(activationDto.getId(), null, exchange, chain))
                .switchIfEmpty(Mono.defer(() -> respondWithJson(exchange, error("Email not found."))));
    }

    private Mono<Void> handleEmailResetPasswordFlow(String keyResetCode, ServerWebExchange exchange, WebFilterChain chain) {
        String resetCode = exchange.getRequest().getQueryParams().getFirst(keyResetCode);
        return resetService.findByCode(resetCode)
                .flatMap(resetDto -> setAuthenticated(resetDto.getId(), null, exchange, chain))
                .switchIfEmpty(Mono.defer(() -> respondWithHtml(exchange, "<h1>Fallo en el filter!</h1>")));
    }

    private Mono<Void> handleSetNewPasswordFlow(String keyCodeToSet, ServerWebExchange exchange, WebFilterChain chain) {
        String codeToSet = exchange.getRequest().getQueryParams().getFirst(keyCodeToSet);
        return resetService.findByCode(codeToSet)
                .flatMap(resetDto -> setAuthenticated(resetDto.getId(), null, exchange, chain))
                .switchIfEmpty(Mono.defer(() -> respondWithHtml(exchange, "<h1>Fallo en el filter!</h1>")));
    }

    private Mono<Void> setAuthenticated(String userEmailOrId, String token, ServerWebExchange exchange, WebFilterChain chain) {
        return userService.findByUsername(userEmailOrId)
                        .map(user -> new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities()))
                .cast(Authentication.class)
                .flatMap(authentication -> chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication)))
                .then();
    }

    private Mono<Void> respondWithHtml(ServerWebExchange exchange, String htmlContent) {
        if (!exchange.getResponse().isCommitted()) {
            exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
            exchange.getResponse().getHeaders().setContentType(MediaType.TEXT_HTML);
            DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(htmlContent.getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(dataBuffer));
        } else {
            return Mono.empty();
        }
    }

    private Mono<Void> respondWithJson(ServerWebExchange exchange, Object response) {
        if (!exchange.getResponse().isCommitted()) {
            exchange.getResponse().setStatusCode(HttpStatus.NOT_FOUND);
            exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
            byte[] jsonBytes = convertToJsonAsBytes(response);
            DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(jsonBytes);
            return exchange.getResponse().writeWith(Mono.just(dataBuffer));
        } else { return Mono.empty(); }
    }

}
