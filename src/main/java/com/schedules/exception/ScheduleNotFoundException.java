package com.schedules.exception;

import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.NOT_FOUND;


public class ScheduleNotFoundException extends ErrorResponseException {

    public ScheduleNotFoundException(String message) {
        super(message);
    }

    public ResponseEntity<String> getResponseEntity() {
        return ResponseEntity.status(NOT_FOUND).body("{\"errorMessage\":\"" + getMessage() + "\"}");
    }
}
