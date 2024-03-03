package com.whatacook.cookers.config.jwt;

import com.whatacook.cookers.utilities.Util;
import com.whatacook.cookers.view.ActivationService;
import com.whatacook.cookers.view.UserService;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
public class JwtRequestFilter implements WebFilter {

    private final ActivationService activationService;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public JwtRequestFilter(ActivationService activationService, UserService userService, JwtUtil jwtUtil) {
        this.activationService = activationService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        String activationCode = exchange.getRequest().getQueryParams().getFirst("activationCode");
        String emailToResend = exchange.getRequest().getQueryParams().getFirst("emailToResend");

        if (Util.notNullOrEmpty(activationCode)) {
            return processActivationCode(activationCode, exchange)
                    .then(chain.filter(exchange));
        } else if (Util.notNullOrEmpty(emailToResend)) {
            return processResendActivationMail(emailToResend, exchange)
                    .then(chain.filter(exchange));
        } else {
            String requestToken = exchange.getRequest().getHeaders().getFirst(jwtUtil.getHeader());

            if (jwtUtil.hasToken(requestToken) && jwtUtil.isValidToken(requestToken)) {

                String tokenWithoutPrefix = jwtUtil.extractPrefix(requestToken);
                String username = jwtUtil.getUsernameFromToken(tokenWithoutPrefix);

                return userService.findByUsername(username)
                        .map(userDetails -> getAuthentication(userDetails, tokenWithoutPrefix))
                        .flatMap(authentication -> chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication)));
            } else {
                return chain.filter(exchange);
            }
        }
    }

    private Authentication getAuthentication(UserDetails userDetails, String token) {
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

    private Mono<Void> processResendActivationMail(String emailToResend, ServerWebExchange exchange) {
        return userService.findDtoByMail(emailToResend)
                .flatMap(userDTO -> activationService.findById(userDTO.get_id()))
                .flatMap(activationDto -> userService.findByUsername(activationDto.getId())
                        .doOnSuccess(userDetails -> setSecurityContext(userDetails, exchange)))
                .then();
    }

    private Mono<Void> processActivationCode(String activationCode, ServerWebExchange exchange) {
        return activationService.findByCode(activationCode)
                .flatMap(activationDto -> userService.findByUsername(activationDto.getId())
                        .doOnSuccess(userDetails -> setSecurityContext(userDetails, exchange)))
                .then();
    }

    private Mono<Void> processJwtToken(String requestToken, ServerWebExchange exchange) {
        return Mono.just(requestToken)
                .filter(jwtUtil::isValidToken)
                .map(jwtUtil::extractPrefix)
                .flatMap(token -> {
                    String userEmailOrId = jwtUtil.getUsernameFromToken(token);
                    return userService.findByUsername(userEmailOrId)
                            .filter(userDetails -> jwtUtil.verifyUserFromToken(token, userDetails))
                            .doOnSuccess(userDetails -> setSecurityContext(userDetails, exchange));
                })
                .then();
    }

    private Mono<Void> setSecurityContext(UserDetails userDetails, ServerWebExchange exchange) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        return Mono.defer(() -> {
            // Establece el contexto de seguridad aquí, pero no intentes devolver el resultado de contextWrite directamente
            return Mono.just(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
                    .then(); // Esto devuelve Mono<Void>, cumpliendo con el tipo esperado
        });
    }


}
