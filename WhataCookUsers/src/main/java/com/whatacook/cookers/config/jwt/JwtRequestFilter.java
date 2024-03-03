package com.whatacook.cookers.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.whatacook.cookers.model.constants.Htmls;
import com.whatacook.cookers.model.responses.Response;
import com.whatacook.cookers.utilities.Util;
import com.whatacook.cookers.view.ActivationService;
import com.whatacook.cookers.view.UserDAO;
import com.whatacook.cookers.view.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import java.util.Objects;

@SuppressWarnings("ReactorTransformationOnMonoVoid")
@Component
public class JwtRequestFilter implements WebFilter {

    private final ActivationService activationService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final UserDAO DAO;

    @Value("${links.wac.logo.small.png}")
    private String wacLogoPngSmall;

    @Value("${SPRING_MAIL_VALIDATION}")
    private String mailToWac;

    @Value("${app.endpoint.users-resend}")
    private String urlToResendActvationMail;

    public JwtRequestFilter(ActivationService activationService, UserService userService, JwtUtil jwtUtil, UserDAO dao) {
        this.activationService = activationService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        DAO = dao;
    }

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
        else {
            return chain.filter(exchange);
        }
    }

    private Mono<Void> handleTokenAuthenticationFlow(ServerWebExchange exchange, WebFilterChain chain) {
        String requestToken = exchange.getRequest().getHeaders().getFirst(jwtUtil.getHeader());
        if (jwtUtil.hasToken(requestToken) && jwtUtil.isValidToken(requestToken)) {
            String tokenWithoutPrefix = jwtUtil.extractPrefix(requestToken);
            String username = jwtUtil.getUsernameFromToken(tokenWithoutPrefix);
            return setAuthenticated(username, tokenWithoutPrefix, exchange, chain);
        } else {
            return chain.filter(exchange);
        }
    }

    private Mono<Void> handleActivationCodeFlow(String keyActivationCode, ServerWebExchange exchange, WebFilterChain chain) {
        String activationCode = exchange.getRequest().getQueryParams().getFirst(keyActivationCode);
        String FAIL_HTML_FOR_ACTIVATION = String.format(Htmls.FailActivation.get(), wacLogoPngSmall, urlToResendActvationMail, mailToWac);
        return activationService.findByCode(activationCode)
                .flatMap(activationDto -> setAuthenticated(activationDto.getId(), null, exchange, chain))
                    .switchIfEmpty(Mono.defer(() -> respondWithHtml(exchange, FAIL_HTML_FOR_ACTIVATION)));
    }

    private Mono<Void> handleEmailResendFlow(String keyEmailToResend, ServerWebExchange exchange, WebFilterChain chain) {
        String emailToResend = exchange.getRequest().getQueryParams().getFirst(keyEmailToResend);

        return Mono.just(Objects.requireNonNull(emailToResend)).filter(Util::isValidEmail).flatMap(DAO::findByEmail)
                .flatMap(userDTO -> activationService.findById(userDTO.get_id()))
                        .flatMap(activationDto -> setAuthenticated(activationDto.getId(), null, exchange, chain))
                .switchIfEmpty(Mono.defer(() -> respondWithJson(exchange, Response.error("Email not found."))));
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
            try {
                byte[] jsonBytes = new ObjectMapper().writeValueAsBytes(response);
                DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(jsonBytes);
                return exchange.getResponse().writeWith(Mono.just(dataBuffer));
            } catch (Exception e) { return Mono.error(e); }
        } else { return Mono.empty(); }
    }

}
