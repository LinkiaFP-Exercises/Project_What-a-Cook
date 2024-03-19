package com.whatacook.cookers.model.exceptions;

import lombok.Getter;
import reactor.core.publisher.Mono;

import java.io.Serial;
import java.util.Map;

@Getter
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

    public static <T> Mono<T> mono(String message) {
        return Mono.error(new UserServiceException(message));
    }

    public static <T> Mono<T> mono(String message, Map<String, Object> errors) {
        return Mono.error(new UserServiceException(message, errors));
    }

    public static <T> Mono<T> mono(Throwable e) {
        return Mono.error(new UserServiceException(e.getMessage()));
    }

    public static void doOnErrorMap(Throwable throwable) {
        throw new UserServiceException(throwable.getMessage());
    }

    public static Throwable onErrorMap(Throwable throwable) {
        return new UserServiceException(throwable.getMessage());
    }

    public static UserServiceException passNotMatch() {
        return new UserServiceException("Password doesn't match!");
    }

}
