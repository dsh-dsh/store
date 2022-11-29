package com.example.store.model.enums;

import com.example.store.utils.Constants;

public enum PaymentType implements EnumeratedInterface {

    TAX_PAYMENT(Constants.TAX_PAYMENT_TYPE),
    CLIENT_PAYMENT(Constants.CLIENT_PAYMENT_TYPE),
    SUPPLIER_PAYMENT(Constants.SUPPLIER_PAYMENT_TYPE),
    OTHER_PAYMENT(Constants.OTHER_PAYMENT_TYPE),
    COST_PAYMENT(Constants.COST_PAYMENT_TYPE),
    SALARY_PAYMENT(Constants.SALARY_PAYMENT_TYPE),
    SALE_CASH_PAYMENT(Constants.SALE_CASH_PAYMENT_TYPE),
    SALE_CARD_PAYMENT(Constants.SALE_CARD_PAYMENT_TYPE),
    SALE_QR_PAYMENT(Constants.SALE_QR_PAYMENT_TYPE);

    private final String value;

    PaymentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static PaymentType getByValue(String value) {
        for(PaymentType paymentType : PaymentType.values()) {
            if(paymentType.value.equalsIgnoreCase(value)) {
                return paymentType;
            }
        }
        return null;
    }
}
