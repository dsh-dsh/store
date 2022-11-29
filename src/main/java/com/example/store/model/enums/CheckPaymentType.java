package com.example.store.model.enums;

import com.example.store.utils.Constants;

public enum CheckPaymentType implements EnumeratedInterface{

    CASH_PAYMENT(Constants.CASH_PAYMENT_TYPE),
    CARD_PAYMENT(Constants.CARD_PAYMENT_TYPE),
    QR_PAYMENT(Constants.QR_PAYMENT_TYPE),
    DELIVERY_PAYMENT(Constants.DELIVERY_PAYMENT_TYPE);

    private String value;

    CheckPaymentType(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    public static CheckPaymentType getByValue(String value) {
        for(CheckPaymentType checkPaymentType : CheckPaymentType.values()) {
            if(checkPaymentType.value.equalsIgnoreCase(value)) {
                return checkPaymentType;
            }
        }
        return null;
    }
}
