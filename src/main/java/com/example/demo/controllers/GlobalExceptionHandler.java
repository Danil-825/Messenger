package com.example.demo.controllers;

import com.example.demo.DTO.ErrorResponse;
import com.example.demo.exceptions.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFoundException(UserNotFoundException e) {
        notFound(e, "User");
        ErrorResponse errorResponse = new ErrorResponse("User not found", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Object> handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        alreadyExists(e, "Email");
        ErrorResponse errorResponse = new ErrorResponse("Email already exists", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(NotificationNotFoundException.class)
    public ResponseEntity<Object> handleNotificationNotFoundException(NotificationNotFoundException e) {
        notFound(e, "Notification");
        ErrorResponse errorResponse = new ErrorResponse("Notification not found", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> errors
                .put(error.getField(), error.getDefaultMessage()));
        log.warn("Validation errors: {}", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(ChatNotFoundException.class)
    public ResponseEntity<Object> handleChatNotFoundException(ChatNotFoundException e) {
        notFound(e, "Chat");
        ErrorResponse errorResponse = new ErrorResponse("User not found", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ChatAlreadyExistsException.class)
    public ResponseEntity<Object> handleChatAlreadyExistsException(ChatAlreadyExistsException e) {
        alreadyExists(e, "Chat");
        ErrorResponse errorResponse = new ErrorResponse("Email already exists", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    private void notFound(Exception e, String objectName) {
        log.warn("{} not found: {}", objectName, e.getMessage());
    }

    private void alreadyExists(Exception e, String objectName) {
        log.warn("{} already exists: {}", objectName, e.getMessage());
    }
}

