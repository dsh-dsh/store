package com.example.store.model.enums;

import com.example.store.utils.Constants;

public enum PriceType implements EnumeratedInterface {

    RETAIL(Constants.RETAIL_PRICE_TYPE),
    DELIVERY(Constants.DELIVERY_PRICE_TYPE);

    private final String value;

    PriceType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PriceType getByValue(String value) {
        for(PriceType priceType : PriceType.values()) {
            if(priceType.value.equalsIgnoreCase(value)) {
                return priceType;
            }
        }
        return null;
    }
}
