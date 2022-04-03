package com.example.store.exceptions;

import com.example.store.utils.Constants;

public class HoldDocumentException  extends RuntimeException {
    public HoldDocumentException() {
        super(Constants.HOLD_FAILED_MESSAGE);
    }
    public HoldDocumentException(String message) {
        super(message);
    }
}
