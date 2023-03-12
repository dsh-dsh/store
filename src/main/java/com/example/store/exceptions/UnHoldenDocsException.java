package com.example.store.exceptions;

import com.example.store.utils.Constants;

public class UnHoldenDocsException extends RuntimeException {
    public UnHoldenDocsException() {
        super(Constants.NOT_HOLDEN_CHECKS_EXIST_MESSAGE);
    }
    public UnHoldenDocsException(String message) {
        super(message);
    }
}
