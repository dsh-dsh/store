package com.example.store.exceptions;

import com.example.store.utils.Constants;
import lombok.Getter;

@Getter
public class HoldDocumentException extends RuntimeException {
    private String info;
    public HoldDocumentException() {
        super(Constants.HOLD_FAILED_MESSAGE);
    }
    public HoldDocumentException(String message) {
        super(message);
    }
    public HoldDocumentException(String message, String info) {
        super(message);
        this.info = info;
    }
}
