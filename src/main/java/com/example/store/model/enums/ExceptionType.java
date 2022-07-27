package com.example.store.model.enums;

import lombok.Getter;

@Getter
public enum ExceptionType {

    COMMON_EXCEPTION(0),
    HOLD_EXCEPTION(1),
    UN_HOLD_EXCEPTION(2);

    private final int value;

    ExceptionType(int value) {
        this.value = value;
    }
}
