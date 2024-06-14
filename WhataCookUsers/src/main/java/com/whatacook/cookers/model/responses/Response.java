package com.whatacook.cookers.model.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.whatacook.cookers.model.exceptions.UserServiceException;
import jakarta.annotation.Nullable;
import reactor.core.publisher.Mono;

import java.util.HashMap;

import static com.whatacook.cookers.utilities.Util.notNullOrEmpty;

/**
 * Standard response structure for API responses.
 * Provides methods to create success and error responses.
 * <p>
 * Annotations:
 * - @SuppressWarnings: Suppresses the specified compiler warnings.
 * - @JsonPropertyOrder: Defines the order of properties in JSON serialization.
 * - @JsonInclude: Specifies inclusion criteria for JSON serialization.
 * <p>
 * Methods:
 * - error(String message): Creates an error response with a message.
 * - monoError(String message): Creates a Mono wrapping an error response with a message.
 * - monoError(Exception e): Creates a Mono wrapping an error response from an exception.
 * - monoError(Throwable e): Creates a Mono wrapping an error response from a throwable.
 * - error(String message, @Nullable Object content): Creates an error response with a message and additional content.
 * - monoError(String message, @Nullable Object content): Creates a Mono wrapping an error response with a message and additional content.
 * - monoError(UserServiceException e): Creates a Mono wrapping an error response from a UserServiceException.
 * - success(String message, @Nullable Object content): Creates a success response with a message and additional content.
 * - setMessage(String message): Sets the message if it's not null or empty.
 * - getMessage(): Gets the message from the response.
 * - isSuccess(): Checks if the response indicates success.
 * - getContent(): Gets the content from the response.
 * - addMessage(String message): Adds a message to the response.
 * - toString(): Returns the string representation of the response.
 *
 * @author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@SuppressWarnings("unused")
@JsonPropertyOrder({"success", "message"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    /**
     * Creates an error response with a message.
     *
     * @param message The error message.
     * @return A Response object indicating an error.
     */
    public static Response error(String message) {
        Response response = new Response();
        response.put(SUCCESS, false);
        response.put(MESSAGE, setMessage(message));
        return response;
    }

    /**
     * Creates a Mono wrapping an error response with a message.
     *
     * @param message The error message.
     * @return A Mono wrapping a Response object indicating an error.
     */
    public static Mono<Response> monoError(String message) {
        return Mono.just(error(message));
    }

    /**
     * Creates a Mono wrapping an error response from an exception.
     *
     * @param e The exception.
     * @return A Mono wrapping a Response object indicating an error.
     */
    public static Mono<Response> monoError(Exception e) {
        return monoError(e.getMessage());
    }

    /**
     * Creates a Mono wrapping an error response from a throwable.
     *
     * @param e The throwable.
     * @return A Mono wrapping a Response object indicating an error.
     */
    public static Mono<Response> monoError(Throwable e) {
        return monoError(e.getMessage());
    }

    /**
     * Creates an error response with a message and additional content.
     *
     * @param message The error message.
     * @param content The additional content.
     * @return A Response object indicating an error.
     */
    public static Response error(String message, @Nullable Object content) {
        Response response = error(message);
        if (content != null)
            response.put(CONTENT, content);
        return response;
    }

    /**
     * Creates a Mono wrapping an error response with a message and additional content.
     *
     * @param message The error message.
     * @param content The additional content.
     * @return A Mono wrapping a Response object indicating an error.
     */
    public static Mono<Response> monoError(String message, @Nullable Object content) {
        return Mono.just(error(message, content));
    }

    /**
     * Creates a Mono wrapping an error response from a UserServiceException.
     *
     * @param e The UserServiceException.
     * @return A Mono wrapping a Response object indicating an error.
     */
    public static Mono<Response> monoError(UserServiceException e) {
        return monoError(e.getMessage(), e.getErrors());
    }

    /**
     * Creates a success response with a message and additional content.
     *
     * @param message The success message.
     * @param content The additional content.
     * @return A Response object indicating success.
     */
    public static Response success(String message, @Nullable Object content) {
        Response response = new Response();
        response.put(SUCCESS, true);
        response.put(MESSAGE, setMessage(message));
        if (content != null)
            response.put(CONTENT, content);
        return response;
    }

    /**
     * Sets the message if it's not null or empty.
     *
     * @param message The message to set.
     * @return The message if not null or empty, otherwise a default error message.
     */
    public static String setMessage(String message) {
        return (notNullOrEmpty(message)) ? message : MESSAGE_ERROR;
    }

    /**
     * Gets the message from the response.
     *
     * @return The message from the response.
     */
    public String getMessage() {
        return (get(MESSAGE) == null) ? MESSAGE_ERROR : (String) get(MESSAGE);
    }

    /**
     * Checks if the response indicates success.
     *
     * @return True if the response indicates success, otherwise false.
     */
    public boolean isSuccess() {
        return (boolean) get(SUCCESS);
    }

    /**
     * Gets the content from the response.
     *
     * @return The content from the response.
     */
    public Object getContent() {
        return map.containsKey(CONTENT) ? map.get(CONTENT) : map.remove(CONTENT);
    }

    /**
     * Adds a message to the response.
     *
     * @param message The message to add.
     */
    public void addMessage(String message) {
        if (notNullOrEmpty(message))
            this.put(MESSAGE, this.get(MESSAGE) + message);
    }

    @Override
    public String toString() {
        return this.map.toString();
    }

    private Response() {
        map = new HashMap<>();
    }

    private void put(String key, Object value) {
        map.put(key, value);
    }

    private Object get(String key) {
        return map.get(key);
    }

    private final HashMap<String, Object> map;
    private final static String MESSAGE_ERROR = "Sorry, this Response has no message!";
    private final static String SUCCESS = "success";
    private final static String MESSAGE = "message";
    private final static String CONTENT = "content";
}
