package com.tncalculator.calculatorapi.exceptions;

import com.tncalculator.calculatorapi.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageService messageService;

    @Autowired
    public RestExceptionHandler(MessageService messageService) {
        super();
        this.messageService = messageService;
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        String errorMessage = getErrorMessage(ex.getMessage(), new Object[]{});
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, errorMessage);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNotExistentEntity(NotFoundException ex) {
        String errorMessage = getErrorMessage(ex.getMessage(), ex.getArgs());
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, errorMessage);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(InvalidOperationArgumentsException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<Object> handleInvalidOperationArgumentsException(InvalidOperationArgumentsException ex) {
        String errorMessage = getErrorMessage(ex.getMessage(), ex.getArgs());
        ApiError apiError = new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, errorMessage);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(InvalidRolesException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<Object> handleInvalidRolesException(InvalidRolesException ex) {
        String errorMessage = getErrorMessage(ex.getMessage(), ex.getArgs());
        ApiError apiError = new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, errorMessage);
        return buildResponseEntity(apiError);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        String errorMessage = getErrorMessage(ex.getMessage(), new Object[]{});
        ApiError apiError = new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, errorMessage);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(IllegalArgumentServiceException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ResponseEntity<Object> handleIllegalArgumentServiceException(IllegalArgumentServiceException ex) {
        String errorMessage = getErrorMessage(ex.getMessage(), ex.getArgs());
        ApiError apiError = new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, errorMessage);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(ForbiddenServiceException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Object> handleForbiddenServiceException(ForbiddenServiceException ex) {
        String errorMessage = getErrorMessage(ex.getMessage(), ex.getArgs());
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, errorMessage);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        String errorMessage = getErrorMessage(ex.getMessage(), new Object[]{});
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, errorMessage);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(CredentialsExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<Object> handleCredentialsExpiredException(CredentialsExpiredException ex) {
        String errorMessage = getErrorMessage(ex.getMessage(), new Object[]{});
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, errorMessage);
        return buildResponseEntity(apiError);
    }

    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                               HttpHeaders headers, HttpStatusCode status,
                                                               WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ApiError apiError = new ApiError(HttpStatus.UNPROCESSABLE_ENTITY, String.format("Validation failed for %s fields", errors.size()), errors);
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Object> handleException(Exception ex) {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
        ex.printStackTrace();
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(UndeclaredThrowableException.class)
    public ResponseEntity<Object> handleUndeclaredThrowableException(UndeclaredThrowableException ex) {
        if(ex.getCause() instanceof NotFoundException) {
            return handleNotExistentEntity((NotFoundException) ex.getCause());
        }
        else if(ex.getCause() instanceof IllegalArgumentException) {
            return handleIllegalArgumentException((IllegalArgumentException) ex.getCause());
        }
        return handleException(ex);
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    private String getErrorMessage(String message, Object[] args) {
        String errorMessage;
        try {
            errorMessage = messageService.getMessage(message, args);
        } catch (NoSuchMessageException ex) {
            errorMessage = message;
        }
        return errorMessage;
    }
}
