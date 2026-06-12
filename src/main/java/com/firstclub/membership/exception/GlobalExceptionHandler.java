package com.firstclub.membership.exception;

import com.firstclub.membership.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .errorCode("VALIDATION_ERROR")
                .message(message)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockException(
            OptimisticLockingFailureException ex,
            HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .errorCode("CONCURRENT_MODIFICATION")
                .message("The resource was modified by another request. Please retry.")
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(ConcurrentModificationException.class)
    public ResponseEntity<ErrorResponse> handleConcurrentModificationException(
            ConcurrentModificationException ex,
            HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .errorCode("CONCURRENT_MODIFICATION")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(MembershipException.class)
    public ResponseEntity<ErrorResponse> handleMembershipException(
            MembershipException ex,
            HttpServletRequest request) {
        HttpStatus status;
        String errorCode;

        if (ex instanceof SubscriptionNotFoundException ||
                ex instanceof PlanNotFoundException ||
                ex instanceof TierNotFoundException) {
            status = HttpStatus.NOT_FOUND;
            errorCode = "NOT_FOUND";
        } else {
            status = HttpStatus.UNPROCESSABLE_ENTITY;
            errorCode = "MEMBERSHIP_ERROR";
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .errorCode(errorCode)
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex,
            HttpServletRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .errorCode("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred")
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}