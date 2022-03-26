package com.example.store.model.enums;

import com.example.store.utils.Constants;

public enum PaymentType {

    TAX_PAYMENT(Constants.TAX_PAYMENT_TYPE),
    CLIENT_PAYMENT(Constants.CLIENT_PAYMENT_TYPE),
    SUPPLIER_PAYMENT(Constants.SUPPLIER_PAYMENT_TYPE),
    OTHER_PAYMENT(Constants.OTHER_PAYMENT_TYPE),
    SALARY_PAYMENT(Constants.SALARY_PAYMENT_TYPE),
    SALE_PAYMENT(Constants.SALE_PAYMENT_TYPE);

    private final String type;

    PaymentType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static PaymentType getByValue(String type) {
        for(PaymentType paymentType : PaymentType.values()) {
            if(paymentType.type.equalsIgnoreCase(type)) {
                return paymentType;
            }
        }
        return null;
    }
}
