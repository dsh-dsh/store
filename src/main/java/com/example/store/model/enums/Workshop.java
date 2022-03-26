package com.example.store.model.enums;

import com.example.store.utils.Constants;

public enum Workshop {

    KITCHEN(Constants.KITCHEN),
    BAR(Constants.BAR),
    NONE(Constants.NONE);

    private final String name;

    Workshop(String value) {
        this.name = value;
    }

    public String getValue() {
        return name;
    }
}
