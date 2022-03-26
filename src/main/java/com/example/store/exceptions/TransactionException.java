package com.example.store.exceptions;

import com.example.store.utils.Constants;

public class TransactionException extends RuntimeException {

    public TransactionException() {
        super(Constants.TRANSACTION_FAILED_MESSAGE);
    }
    public TransactionException(String message) {
        super(message);
    }
}
