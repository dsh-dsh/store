package com.example.store.model.enums;

import com.example.store.utils.Constants;

public enum PriceType {

    RETAIL(Constants.RETAIL_PRICE_TYPE),
    DELIVERY(Constants.DELIVERY_PRICE_TYPE);

    private final String type;

    PriceType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static PriceType getByValue(String type) {
        for(PriceType priceType : PriceType.values()) {
            if(priceType.type.equalsIgnoreCase(type)) {
                return priceType;
            }
        }
        return null;
    }
}
