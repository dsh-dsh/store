package com.example.store.exceptions;

import com.example.store.utils.Constants;
import lombok.Getter;

@Getter
public class TransactionException extends RuntimeException {
    private String info;
    public TransactionException() {
        super(Constants.TRANSACTION_FAILED_MESSAGE);
    }
    public TransactionException(String message) {
        super(message);
    }
    public TransactionException(String message, String info) {
        super(message);
        this.info = info;
    }
}
