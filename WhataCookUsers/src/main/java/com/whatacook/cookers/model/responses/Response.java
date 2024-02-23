package com.whatacook.cookers.model.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.lang.Nullable;

import java.util.HashMap;

import static com.whatacook.cookers.utilities.Util.notNullOrEmpty;

@JsonPropertyOrder({"success", "message"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response {

    public static Response error(String message) {
        Response response = new Response();
        response.put(SUCCESS, false);
        response.put(MESSAGE, setMessage(message));
        return response;
    }

    public static Response error(String message, @Nullable Object content) {
        Response response = new Response();
        response.put(SUCCESS, false);
        response.put(MESSAGE, setMessage(message));
        if (content != null)
            response.put(CONTENT, content);
        return response;
    }

    public static Response success(String message, @Nullable Object content) {
        Response response = new Response();
        response.put(SUCCESS, true);
        response.put(MESSAGE, setMessage(message));
        if (content != null)
            response.put(CONTENT, content);
        return response;
    }

    public static String setMessage(String message) {
        return (notNullOrEmpty(message)) ? message : MESSAGE_ERROR;
    }

    public String getMessage() {
        return (get(MESSAGE) == null) ? MESSAGE_ERROR : (String) get(MESSAGE);
    }

    public boolean isSuccess() {
        return (boolean) get(SUCCESS);
    }

    public Object getContent() {
        return map.containsKey(CONTENT) ? map.get(CONTENT) : map.remove(CONTENT);
    }

    public void addMessage(String message) {
        if (notNullOrEmpty(message))
            this.put(MESSAGE, this.get(MESSAGE) + message);
    }

    @Override
    public String toString() {
        return this.map.toString();
    }

    private Response() {
        map = new HashMap<String, Object>();
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
