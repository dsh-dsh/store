package com.example.store.model.enums;

import com.example.store.utils.Constants;

public enum StorageType {

    STORE_STORE(Constants.STORE_STORE_TYPE),
    CAFE_STORE(Constants.CAFE_STORE_TYPE),
    RETAIL_STORE(Constants.RETAIL_STORE_TYPE);

    private final String type;

    StorageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
