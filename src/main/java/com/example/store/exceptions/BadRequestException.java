package com.example.store.exceptions;

import com.example.store.model.enums.ExceptionType;
import com.example.store.utils.Constants;
import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

    private final ExceptionType exceptionType;
    private String info;

    public BadRequestException() {
        super(Constants.BAD_REQUEST_MESSAGE);
        this.exceptionType = ExceptionType.COMMON_EXCEPTION;
    }

    public BadRequestException(String message) {
        super(message);
        this.exceptionType = ExceptionType.COMMON_EXCEPTION;
    }

    public BadRequestException(String message, String info) {
        super(message);
        this.exceptionType = ExceptionType.COMMON_EXCEPTION;
        this.info = info;
    }

    public BadRequestException(String message, ExceptionType exceptionType) {
        super(message);
        this.exceptionType = exceptionType;
    }

}
