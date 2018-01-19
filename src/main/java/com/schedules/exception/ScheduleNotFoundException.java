package com.schedules.exception;

import org.springframework.http.ResponseEntity;


public class ScheduleNotFoundException extends RuntimeException {

    public ScheduleNotFoundException(String message) {
        super(message);
    }

    public ResponseEntity<String> getResponseEntity() {
        return ResponseEntity.badRequest().body(getMessage());
    }
}
