package com.example.store.exceptions;

import lombok.Getter;

@Getter
public class WarningException  extends RuntimeException{

    private String info;

    public WarningException(String message, String info) {
        super(message);
        this.info = info;
    }
}