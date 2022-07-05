package com.example.store.model.enums;

import com.example.store.utils.Constants;

public enum PeriodicValueType implements EnumeratedInterface {

    NET(Constants.NET_TYPE),
    GROSS(Constants.GROSS_TYPE),
    ENABLE(Constants.ENABLE_TYPE);

    private final String value;

    PeriodicValueType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PeriodicValueType getByValue(String value) {
        for(PeriodicValueType periodicValueType : PeriodicValueType.values()) {
            if(periodicValueType.value.equalsIgnoreCase(value)) {
                return periodicValueType;
            }
        }
        return null;
    }

}
