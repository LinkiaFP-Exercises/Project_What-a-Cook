package com.whatacook.cookers.model.exceptions;

import lombok.Getter;
import reactor.core.publisher.Mono;

import java.io.Serial;
import java.util.Map;

/**
 * Custom exception for user service-related errors.
 * This exception provides additional context by allowing errors to be stored in a map.
 * <p>
 * Fields:
 * - serialVersionUID: Unique identifier for serializable classes.
 * - errors: A map containing error details.
 * <p>
 * Methods:
 * - UserServiceException(String message): Constructor with a message.
 * - UserServiceException(String message, Map<String, Object> errors): Constructor with a message and error details.
 * - <T> Mono<T> mono(String message): Creates a Mono that emits this exception with a message.
 * - <T> Mono<T> mono(String message, Map<String, Object> errors): Creates a Mono that emits this exception with a message and error details.
 * - <T> Mono<T> mono(Throwable e): Creates a Mono that emits this exception based on another throwable.
 * - void doOnErrorMap(Throwable throwable): Throws this exception based on another throwable.
 * - Throwable onErrorMap(Throwable throwable): Returns this exception based on another throwable.
 * - UserServiceException passNotMatch(): Returns a new instance of this exception indicating a password mismatch.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@Getter
public final class UserServiceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -1266524152146144087L;
    private Map<String, Object> errors;

    /**
     * Constructs a new UserServiceException with the specified detail message.
     *
     * @param message The detail message.
     */
    public UserServiceException(String message) {
        super(message);
    }

    /**
     * Constructs a new UserServiceException with the specified detail message and errors.
     *
     * @param message The detail message.
     * @param errors  The errors map.
     */
    public UserServiceException(String message, Map<String, Object> errors) {
        super(message);
        this.errors = errors;
    }

    /**
     * Creates a Mono that emits this exception with the specified message.
     *
     * @param message The detail message.
     * @param <T>     The type parameter.
     * @return A Mono that emits this exception.
     */
    public static <T> Mono<T> mono(String message) {
        return Mono.error(new UserServiceException(message));
    }

    /**
     * Creates a Mono that emits this exception with the specified message and errors.
     *
     * @param message The detail message.
     * @param errors  The errors map.
     * @param <T>     The type parameter.
     * @return A Mono that emits this exception.
     */
    public static <T> Mono<T> mono(String message, Map<String, Object> errors) {
        return Mono.error(new UserServiceException(message, errors));
    }

    /**
     * Creates a Mono that emits this exception based on another throwable.
     *
     * @param e   The throwable to base the exception on.
     * @param <T> The type parameter.
     * @return A Mono that emits this exception.
     */
    public static <T> Mono<T> mono(Throwable e) {
        return Mono.error(new UserServiceException(e.getMessage()));
    }

    /**
     * Throws this exception based on another throwable.
     *
     * @param throwable The throwable to base the exception on.
     */
    public static void doOnErrorMap(Throwable throwable) {
        throw new UserServiceException(throwable.getMessage());
    }

    /**
     * Returns this exception based on another throwable.
     *
     * @param throwable The throwable to base the exception on.
     * @return This exception.
     */
    public static Throwable onErrorMap(Throwable throwable) {
        return new UserServiceException(throwable.getMessage());
    }

    /**
     * Returns a new instance of this exception indicating a password mismatch.
     *
     * @return A new UserServiceException indicating a password mismatch.
     */
    public static UserServiceException passNotMatch() {
        return new UserServiceException("Password doesn't match!");
    }
}
