package com.schedules.exception;


import org.springframework.http.ResponseEntity;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class BadRequestException extends ErrorResponseException {

    BadRequestException(String message) {
        super(message);
    }

    @Override
    public ResponseEntity<String> getResponseEntity() {
        return ResponseEntity.status(BAD_REQUEST).body("{\"errorMessage\":\"" + getMessage() + "\"}");
    }
}
