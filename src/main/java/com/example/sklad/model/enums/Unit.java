package com.example.sklad.model.enums;

import com.example.sklad.utils.Constants;

public enum Unit {

    KG(Constants.KG),
    LITER(Constants.LITER),
    PIECE(Constants.PIECE),
    PORTION(Constants.PORTION);

    private final String name;

    Unit(String value) {
        this.name = value;
    }

    public String getValue() {
        return name;
    }
}
