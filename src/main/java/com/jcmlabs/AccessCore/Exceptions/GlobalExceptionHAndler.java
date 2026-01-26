package com.jcmlabs.AccessCore.Exceptions;

import com.jcmlabs.AccessCore.Utilities.BaseResponse;
import com.jcmlabs.AccessCore.Utilities.ResponseCode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Authentication errors (wrong username/password)
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseResponse<Object>> handleBadCredentials(BadCredentialsException ex) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(BaseResponse.error(ResponseCode.UNAUTHORIZED, "Invalid username or password"));
    }

    /**
     * DTO validation errors (@NotNull, @NotBlank, @Email, etc.)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleValidationErrors(MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(BaseResponse.error(ResponseCode.VALIDATION_ERROR, "Validation failed", errors));
    }

    /**
     * Database constraint violations (unique keys, foreign keys, etc.)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse<Object>> handleConstraintViolation(DataIntegrityViolationException ex) {

        String rootMessage = ex.getMostSpecificCause().getMessage();

        if (rootMessage != null && rootMessage.contains("id_number")) {
            return ResponseEntity.badRequest().body(BaseResponse.error(ResponseCode.EXISTS, "ID number already exists"));
        }

        return ResponseEntity.internalServerError().body(BaseResponse.error(ResponseCode.INTERNAL_ERROR, "Database constraint violation"));
    }

    /**
     * Custom business rule violations
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<BaseResponse<Object>> handleBusinessException(BusinessException ex) {

        return ResponseEntity.badRequest().body(BaseResponse.error(ex.getCode(), ex.getMessage()));
    }

    /**
     * Fallback for all unhandled exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleUnexpected(Exception ex) {

        return ResponseEntity.internalServerError().body(BaseResponse.error(ResponseCode.INTERNAL_ERROR, "Something went wrong. Please try again."));
    }
}
