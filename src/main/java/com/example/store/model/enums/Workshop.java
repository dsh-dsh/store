package com.example.store.model.enums;

import com.example.store.utils.Constants;

public enum Workshop implements EnumeratedInterface{

    KITCHEN(Constants.KITCHEN),
    BAR(Constants.BAR),
    NONE(Constants.NONE);

    private final String value;

    Workshop(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
