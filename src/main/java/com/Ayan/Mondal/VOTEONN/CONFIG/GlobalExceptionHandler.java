package com.Ayan.Mondal.VOTEONN.CONFIG;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * This method will catch any exception you throw when a user
     * tries to register with an email that already exists.
     * I am assuming your service throws an IllegalArgumentException.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {

        // Create a clean JSON error body
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", System.currentTimeMillis());
        body.put("status", HttpStatus.BAD_REQUEST.value()); // 400
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage()); // This will contain "Email Already Exist"
        body.put("path", request.getDescription(false).replace("uri=", ""));

        // Return a 400 Bad Request instead of a 404 or 500
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // You can add more handlers here for other exceptions
    // For example, for your login:
    @ExceptionHandler(org.springframework.security.core.AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", System.currentTimeMillis());
        body.put("status", HttpStatus.UNAUTHORIZED.value()); // 401
        body.put("error", "Unauthorized");
        body.put("message", "Invalid email or password");
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }
}