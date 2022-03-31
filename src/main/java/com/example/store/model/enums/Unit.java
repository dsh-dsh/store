package com.example.store.model.enums;

import com.example.store.utils.Constants;

public enum Unit implements EnumeratedInterface {

    KG(Constants.KG),
    LITER(Constants.LITER),
    PIECE(Constants.PIECE),
    PORTION(Constants.PORTION),
    NONE(Constants.NONE);

    private final String value;

    Unit(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
