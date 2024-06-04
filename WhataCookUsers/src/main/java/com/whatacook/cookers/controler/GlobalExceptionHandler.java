package com.whatacook.cookers.controler;

import com.whatacook.cookers.model.responses.Response;
import io.jsonwebtoken.ClaimJwtException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.core.codec.DecodingException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.MethodNotAllowedException;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.joining("; "))));

        return createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid or incorrect format!!!", errorMsg);
    }

    @ExceptionHandler({WebExchangeBindException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response handleValidationExceptions(WebExchangeBindException ex) {
        @SuppressWarnings("DataFlowIssue")
        Map<String, String> errors = ex.getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        FieldError::getDefaultMessage,
                        (existing, replacement) -> existing));

        return createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid or incorrect format!!!", errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response handleConstraintViolationException(ConstraintViolationException ex) {
        var errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> {
                            String path = violation.getPropertyPath().toString();
                            return path.substring(path.lastIndexOf('.') + 1); // Retorna solo el nombre del campo
                        },
                        ConstraintViolation::getMessage, // El mensaje de error para esa violación
                        (existingValue, newValue) -> existingValue // En caso de campos duplicados, mantiene el primer mensaje de error encontrado
                ));


        return createErrorResponse(HttpStatus.BAD_REQUEST, "Validation error", errors);
    }


    @ExceptionHandler({DecodingException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response handleValidationExceptions(DecodingException ex) {
        String errorMessage = "Invalid request body or not present: A valid request body is required.";
        return createErrorResponse(HttpStatus.BAD_REQUEST, errorMessage, ex);
    }

    @ExceptionHandler({ HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MethodNotAllowedException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response handleInvalidRequest(Exception ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid or incorrect requisition!!!", ex);
    }

    @ExceptionHandler({ EmptyResultDataAccessException.class, NoSuchElementException.class })
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Response handleCantFoundWhatYouWant(Exception ex) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Couldn't find what you want", ex);
    }

    @ExceptionHandler({NoResourceFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public Response handleRequestNotFound(Exception ex) {
        return createErrorResponse(HttpStatus.NOT_FOUND, "SORRY BABY, the fault is ours!!!", ex);
    }

    @ExceptionHandler({JwtException.class, ExpiredJwtException.class, ClaimJwtException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public Response handleJwtException(Exception ex) {
        return createErrorResponse(HttpStatus.UNAUTHORIZED, "Please, Log in again!!!", ex);
    }

    private Response createErrorResponse(HttpStatus status, String customMessage, Exception ex) {
        return Response.error(httpMessageError(status, customMessage), Map.of("ERROR", ex.getMessage().split(":")[0]));
    }

    @SuppressWarnings({"rawtypes", "SameParameterValue"})
    private Response createErrorResponse(HttpStatus status, String customMessage, Map map) {
        return Response.error(httpMessageError(status, customMessage), map);
    }

    private static String httpMessageError(HttpStatus status, String msg) {
        return String.format("[%s - %d] 8==> %s", status.getReasonPhrase().toUpperCase(), status.value(), msg);
    }

}
