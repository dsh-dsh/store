package com.example.store.model.enums;

import com.example.store.utils.Constants;

public enum QuantityType implements EnumeratedInterface {

    NET(Constants.NET_TYPE),
    GROSS(Constants.GROSS_TYPE),
    ENABLE(Constants.ENABLE_TYPE);

    private final String value;

    QuantityType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static QuantityType getByValue(String value) {
        for(QuantityType quantityType : QuantityType.values()) {
            if(quantityType.value.equalsIgnoreCase(value)) {
                return quantityType;
            }
        }
        return null;
    }

}
