package com.example.store.model.enums;

import com.example.store.utils.Constants;

public enum QuantityType {

    NET(Constants.NET_TYPE),
    GROSS(Constants.GROSS_TYPE);

    private final String type;

    QuantityType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static QuantityType getByValue(String type) {
        for(QuantityType quantityType : QuantityType.values()) {
            if(quantityType.type.equalsIgnoreCase(type)) {
                return quantityType;
            }
        }
        return null;
    }

}
