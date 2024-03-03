package com.whatacook.cookers.model.exceptions;

import jakarta.validation.constraints.NotNull;
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

    private Map<String, Object> errors;

    public UserServiceException(String message) { super(message); }

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

}
