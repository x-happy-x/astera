package ru.astera.backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            CustomerNotFoundException.class,
            EquipmentNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(Exception ex) {
        log.warn("Not found error", ex);
        return handleException(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            CustomerAlreadyExistsException.class,
            UserAlreadyExistsException.class,
            EquipmentAlreadyExistsException.class
    })
    public ResponseEntity<ErrorResponse> handleConflicts(Exception ex) {
        log.warn("Conflict error", ex);
        return handleException(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        log.warn("Invalid credentials", ex);
        return handleException(ex, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class
    })
    public ResponseEntity<ValidationErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Ошибки валидации",
                OffsetDateTime.now(),
                errors
        );
        log.warn("Validation error", ex);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler({
            IllegalArgumentException.class
    })
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Bad request", ex);
        return handleException(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex) {
        log.error("Internal server error", ex);
        return handleException(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private static ResponseEntity<ErrorResponse> handleException(Exception ex, HttpStatus status) {
        ErrorResponse error = new ErrorResponse(
                status.value(),
                ex.getMessage(),
                OffsetDateTime.now()
        );
        return ResponseEntity.status(status).body(error);
    }
}