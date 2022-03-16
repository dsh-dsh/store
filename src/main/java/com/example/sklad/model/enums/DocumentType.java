package com.example.sklad.model.enums;

import com.example.sklad.utils.Constants;

public enum DocumentType {

    CHECK_DOC(Constants.CHECK_DOC_TYPE),
    CREDIT_ORDER_DOC(Constants.CREDIT_ORDER_DOC_TYPE),
    WITHDRAW_DOC_DOC(Constants.WITHDRAW_DOC_DOC_TYPE),
    POSTING_DOC(Constants.POSTING_DOC_TYPE),
    RECEIPT_DOC(Constants.RECEIPT_DOC_TYPE),
    MOVEMENT_DOC(Constants.MOVEMENT_DOC_TYPE),
    WRITE_OFF_DOC(Constants.WRITE_OFF_DOC_TYPE),
    REQUEST_DOC(Constants.REQUEST_DOC_TYPE),
    INVENTORY_DOC(Constants.REQUEST_DOC_TYPE);

    private final String value;

    DocumentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static DocumentType getByValue(String value) {
        for(DocumentType docType : DocumentType.values()) {
            if(docType.value.equalsIgnoreCase(value)) {
                return docType;
            }
        }
        return null;
    }
}
