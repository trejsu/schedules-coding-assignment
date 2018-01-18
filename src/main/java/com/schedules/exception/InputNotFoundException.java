package com.schedules.exception;


import org.springframework.http.ResponseEntity;

public class InputNotFoundException extends RuntimeException {

    public InputNotFoundException(String message) {
        super(message);
    }

    public ResponseEntity<String> getResponseEntity() {
        return ResponseEntity.badRequest().body(getMessage());
    }
}
