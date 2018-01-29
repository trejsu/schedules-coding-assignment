package com.schedules.exception;


public class JobValidationException extends BadRequestException {

    public JobValidationException(String message) {
        super(message);
    }

}
