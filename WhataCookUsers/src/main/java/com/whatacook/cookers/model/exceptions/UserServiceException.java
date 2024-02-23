package com.whatacook.cookers.model.exceptions;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@ToString
public final class UserServiceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -1266524152146144087L;

    private static final String PREFIX = "UserServiceException 8==> %s!!!";

    private Map<String, String> errors;

    public UserServiceException(String message) {
        super(String.format(PREFIX, message));
    }

    public UserServiceException(String message, Map<String, String> errors) {
        super(String.format(PREFIX, message));
        this.errors = errors;
    }

    public static void throwsUp(String message) {
        throw new UserServiceException(message);
    }

    public static UserServiceException withMsg(String message) {
        return new UserServiceException(message);
    }

    public static UserServiceException withErrors(String message, Map<String, String> errors) {
        return new UserServiceException(message, errors);
    }
}
