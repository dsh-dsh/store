package com.example.store.exceptions;

import com.example.store.utils.Constants;

public class UnHoldDocumentException extends RuntimeException {
    public UnHoldDocumentException() {
        super(Constants.UN_HOLD_FORBIDDEN_MESSAGE);
    }
    public UnHoldDocumentException(String message) {
        super(message);
    }
}
