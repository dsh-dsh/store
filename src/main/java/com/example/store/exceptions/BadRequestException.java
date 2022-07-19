package com.example.store.exceptions;

import com.example.store.utils.Constants;
import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    private ExceptionType exceptionType;

    public BadRequestException() {
        super(Constants.BAD_REQUEST_MESSAGE);
        this.exceptionType = ExceptionType.COMMON_EXCEPTION;
    }

    public BadRequestException(String message) {
        super(message);
        this.exceptionType = ExceptionType.COMMON_EXCEPTION;
    }

    public BadRequestException(String message, ExceptionType exceptionType) {
        super(message);
        this.exceptionType = exceptionType;
    }

}
