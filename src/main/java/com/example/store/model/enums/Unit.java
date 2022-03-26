package com.example.store.model.enums;

import com.example.store.utils.Constants;

public enum Unit {

    KG(Constants.KG),
    LITER(Constants.LITER),
    PIECE(Constants.PIECE),
    PORTION(Constants.PORTION),
    NONE(Constants.NONE);

    private final String name;

    Unit(String value) {
        this.name = value;
    }

    public String getValue() {
        return name;
    }
}
