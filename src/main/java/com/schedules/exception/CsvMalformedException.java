package com.schedules.exception;

import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class CsvMalformedException extends ErrorResponseException {

    public CsvMalformedException(String message) {
        super(message);
    }

    public ResponseEntity<String> getResponseEntity() {
        return ResponseEntity.status(BAD_REQUEST).body("{\"errorMessage\":\"" + getMessage() + "\"}");
    }
}
