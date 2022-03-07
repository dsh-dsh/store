package com.example.sklad.model.enums;

import com.example.sklad.utils.Constants;

public enum DocumentType {

    CHECK_DOC(Constants.CHECK_DOC_TYPE),
    POSTING_DOC(Constants.POSTING_DOC_TYPE),
    RECEIPT_DOC(Constants.RECEIPT_DOC_TYPE),
    MOVEMENT_DOC(Constants.MOVEMENT_DOC_TYPE),
    WRITE_OFF_DOC(Constants.WRITE_OFF_DOC_TYPE);

    private final String type;

    DocumentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
