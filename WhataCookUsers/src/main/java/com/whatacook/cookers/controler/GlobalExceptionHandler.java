package com.whatacook.cookers.controler;

import com.whatacook.cookers.model.responses.Response;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errorMsg = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.groupingBy(FieldError::getField,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.joining("; "))));

        return Response.error(httpMessageError(HttpStatus.BAD_REQUEST, "Invalid or incorrect format!!!"), errorMsg);
    }

    @ExceptionHandler({ HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Response handleInvalidRequest(Exception ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid or incorrect requisition!!!", ex);
    }

    @ExceptionHandler({ RuntimeException.class })
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public Response handleRequestNotFound(Exception ex) {
        return createErrorResponse(HttpStatus.NOT_FOUND, "SORRY BABY, the fault is ours!!!", ex);
    }

    @ExceptionHandler({ EmptyResultDataAccessException.class, NoSuchElementException.class })
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Response handleCantFoundWhatYouWant(Exception ex) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Couldn't find what you want", ex);
    }

    private Response createErrorResponse(HttpStatus status, String customMessage, Exception ex) {
        return Response.error(httpMessageError(status, customMessage), Map.of("ERROR", ex.getMessage().split(":")[0]));
    }

    private static String httpMessageError(HttpStatus status, String msg) {
        return String.format("[%s - %d] 8==> %s", status.getReasonPhrase().toUpperCase(), status.value(), msg);
    }

}
