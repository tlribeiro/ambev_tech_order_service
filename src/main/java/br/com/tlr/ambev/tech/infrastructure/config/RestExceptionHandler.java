package br.com.tlr.ambev.tech.infrastructure.config;

import br.com.tlr.ambev.tech.application.domain.exception.DuplicateOrderException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(DuplicateOrderException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateOrderException(DuplicateOrderException ex) {
        Map<String, String> errorDetails = new HashMap<>();

        errorDetails.put("error", "Ocorreu um erro de negócio");
        errorDetails.put("details", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorDetails);
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(WebExchangeBindException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String fullPath = violation.getPropertyPath().toString();
            String fieldName = fullPath.contains(".") ? fullPath.substring(fullPath.lastIndexOf('.') + 1) : fullPath; // Extrai o último segmento do caminho
            String message = violation.getMessage();
            errors.put(fieldName, message);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUncaughtException(Exception ex) {
        Map<String, String> errorDetails = new HashMap<>();

        errorDetails.put("error", "Ocorreu um erro inesperado");
        errorDetails.put("details", ex.getMessage());

        log.error("Exceção não tratada", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }
}
