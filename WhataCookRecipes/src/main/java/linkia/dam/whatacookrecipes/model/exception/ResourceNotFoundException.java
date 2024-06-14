package linkia.dam.whatacookrecipes.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource is not found.
 * This exception results in a response with a 404 Not Found HTTP status.
 *
 * @Author <a href="https://about.me/prof.guazina">Fauno Guazina</a>
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message the detail message.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
