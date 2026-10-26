package com.aydsii.demo.cu09.exception;

import com.aydsii.demo.cu09.dto.ApiResponseDTO;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Maneja errores de Bean Validation (@Positive en PathVariables)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleConstraintViolationException(ConstraintViolationException ex) {
        String mensaje = ex.getConstraintViolations().iterator().next().getMessage();
        ApiResponseDTO<Object> response = new ApiResponseDTO<>(400, mensaje, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponseDTO<Object> response = new ApiResponseDTO<>(400, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleRuntimeException(RuntimeException ex) {
        ApiResponseDTO<Object> response = new ApiResponseDTO<>(404, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}