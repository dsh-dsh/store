package com.example.store.exceptions;

import com.example.store.utils.Constants;
import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    private ExceptionType exceptionType;

    public BadRequestException() {
        super(Constants.BAD_REQUEST_MESSAGE);
    }

    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, ExceptionType exceptionType) {
        super(message);
        this.exceptionType = exceptionType;
    }

}
