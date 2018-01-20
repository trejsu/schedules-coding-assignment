package com.schedules.exception;

import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class JobValidationException extends ErrorResponseException {

    public JobValidationException(String message) {
        super(message);
    }

    public ResponseEntity<String> getResponseEntity() {
        return ResponseEntity.status(BAD_REQUEST).body("{\"errorMessage\":\"" + getMessage() + "\"}");
    }
}
