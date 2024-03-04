package com.whatacook.cookers.model.exceptions;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import reactor.core.publisher.Mono;

import java.io.Serial;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@NoArgsConstructor
@Getter
@Setter
@ToString
public final class UserServiceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -1266524152146144087L;
    private Map<String, Object> errors;

    public UserServiceException(String message) {
        super(message);
    }

    public UserServiceException(String message, Map<String, Object> errors) {
        super(message);
        this.errors = errors;
    }

    @NotNull
    public static UserServiceException pull(String message) {
        return new UserServiceException(message);
    }

    public static void throwUp(String message) {
        throw new UserServiceException(message);
    }

    @NotNull
    public static UserServiceException pull(String message, Map<String, Object> errors) {
        return new UserServiceException(message, errors);
    }

    public static <T> Mono<T> mono(String message) {
        return Mono.error(new UserServiceException(message));
    }

    public static <T> Mono<T> mono(String message, Map<String, Object> errors) {
        return Mono.error(new UserServiceException(message, errors));
    }

    private static String joinMsgs(String message, Throwable throwable) {
        return message + " 8==> " + throwable.getMessage();
    }

    public static Consumer<? super Throwable> onErrorMap(String message) {
        return throwable -> new UserServiceException(joinMsgs(message, throwable));
    }

    public static Function<Throwable, UserServiceException> onErrorMap(String message, Map<String, Object> errors) {
        return throwable -> new UserServiceException(message, errors);
    }

    public static void onErrorMap(Throwable throwable) {
        throw new UserServiceException(throwable.getMessage());
    }
}
