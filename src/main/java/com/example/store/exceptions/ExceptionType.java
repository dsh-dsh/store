package com.example.store.exceptions;

import lombok.Getter;

@Getter
public enum ExceptionType {

    COMMON_EXCEPTION(0),
    HOLD_EXCEPTION(1);

    private final int value;

    ExceptionType(int value) {
        this.value = value;
    }
}
