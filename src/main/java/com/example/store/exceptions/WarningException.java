package com.example.store.exceptions;

import com.example.store.model.enums.ExceptionType;
import lombok.Getter;

@Getter
public class WarningException  extends RuntimeException{

    private String info;
    private final ExceptionType exceptionType;

    public WarningException(String message, String info) {
        super(message);
        this.info = info;
        this.exceptionType = ExceptionType.COMMON_EXCEPTION;
    }
    public WarningException(String message, ExceptionType exceptionType, String info) {
        super(message);
        this.info = info;
        this.exceptionType = exceptionType;
    }
}