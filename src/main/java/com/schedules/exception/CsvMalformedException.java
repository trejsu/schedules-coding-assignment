package com.schedules.exception;

public class CsvMalformedException extends BadRequestException {

    public CsvMalformedException(String message) {
        super(message);
    }

}
