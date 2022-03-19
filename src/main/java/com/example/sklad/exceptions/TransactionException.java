package com.example.sklad.exceptions;

import com.example.sklad.utils.Constants;

public class TransactionException extends RuntimeException {

    public TransactionException() {
        super(Constants.TRANSACTION_FAILED_MESSAGE);
    }
    public TransactionException(String message) {
        super(message);
    }
}
