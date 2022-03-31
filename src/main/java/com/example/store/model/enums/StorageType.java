package com.example.store.model.enums;

import com.example.store.utils.Constants;

public enum StorageType implements EnumeratedInterface {

    STORE_STORE(Constants.STORE_STORE_TYPE),
    CAFE_STORE(Constants.CAFE_STORE_TYPE),
    RETAIL_STORE(Constants.RETAIL_STORE_TYPE);

    private final String value;

    StorageType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
