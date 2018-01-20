package com.schedules.exception;


import org.springframework.http.ResponseEntity;

public abstract class ErrorResponseException extends RuntimeException {

    ErrorResponseException(String message) {
        super(message);
    }

    public abstract ResponseEntity<String> getResponseEntity();
}
