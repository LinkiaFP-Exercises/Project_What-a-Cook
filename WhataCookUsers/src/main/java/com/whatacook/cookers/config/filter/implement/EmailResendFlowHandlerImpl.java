package com.whatacook.cookers.config.filter.implement;

import com.whatacook.cookers.config.filter.AuthenticationManager;
import com.whatacook.cookers.config.filter.EmailResendFlowHandler;
import com.whatacook.cookers.service.ActivationService;
import com.whatacook.cookers.service.contracts.UserDao;
import com.whatacook.cookers.utilities.Util;
import lombok.AllArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static com.whatacook.cookers.model.responses.Response.error;
import static com.whatacook.cookers.utilities.Util.convertToJsonAsBytes;

@AllArgsConstructor @Component
public class EmailResendFlowHandlerImpl implements EmailResendFlowHandler {

    private final UserDao DAO;
    private final ActivationService activationService;
    private final AuthenticationManager authenticationManager;

    @SuppressWarnings("ReactorTransformationOnMonoVoid")
    @Override
    public Mono<Void> handle(String keyEmailToResend, ServerWebExchange exchange, WebFilterChain chain) {
        String emailToResend = exchange.getRequest().getQueryParams().getFirst(keyEmailToResend);

        return Mono.just(Objects.requireNonNull(emailToResend)).filter(Util::isValidEmail).flatMap(DAO::findByEmail)
                .flatMap(userDTO -> activationService.findById(userDTO.get_id()))
                .flatMap(activationDto -> authenticationManager.setAuthenticated(activationDto.getId(), null, exchange, chain))
                .switchIfEmpty(Mono.defer(() -> respondWithJson(exchange, error("Email not found."))));
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
