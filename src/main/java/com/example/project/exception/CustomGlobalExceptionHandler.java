package com.example.project.exception;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        Map<String, Object> bodyToResponce = new LinkedHashMap<>();
        bodyToResponce.put("timestamp", LocalDateTime.now());
        bodyToResponce.put("status", HttpStatus.BAD_REQUEST);
        List<String> errors = ex.getBindingResult().getAllErrors().stream()
                .map(this::getErrorMessage)
                .toList();
        bodyToResponce.put("errors", errors);
        return new ResponseEntity<>(bodyToResponce, headers, status);
    }

    private String getErrorMessage(ObjectError error) {
        if (error instanceof FieldError) {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            return field + " " + message;
        }
        return error.getDefaultMessage();
    }

    @ExceptionHandler(value = {EntityNotFoundException.class})
    protected ResponseEntity<Object> handleEntityNotFoundException(
            EntityNotFoundException ex,
            WebRequest request
    ) {
        Map<String, Object> bodyToResponce = new LinkedHashMap<>();
        bodyToResponce.put("timestamp", LocalDateTime.now());
        bodyToResponce.put("status", HttpStatus.NOT_FOUND.value());
        bodyToResponce.put("error", ex.getClass() + " Not Found");
        bodyToResponce.put("message", ex.getMessage());
        return new ResponseEntity<>(bodyToResponce, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {RegistrationException.class})
    protected ResponseEntity<Object> handlRegistrationException(
            EntityNotFoundException ex,
            WebRequest request
    ) {
        Map<String, Object> bodyToResponce = new LinkedHashMap<>();
        bodyToResponce.put("timestamp", LocalDateTime.now());
        bodyToResponce.put("status", HttpStatus.UNAUTHORIZED.value());
        bodyToResponce.put("error", ex.getClass() + " Unauthorized access");
        bodyToResponce.put("message", ex.getMessage());
        return new ResponseEntity<>(bodyToResponce, HttpStatus.UNAUTHORIZED);
    }
}
