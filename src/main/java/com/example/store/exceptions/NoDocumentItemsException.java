package com.example.store.exceptions;

import com.example.store.utils.Constants;

public class NoDocumentItemsException extends RuntimeException {
    public NoDocumentItemsException() {
        super(Constants.NO_DOCUMENT_ITEMS_MESSAGE);
    }
    public NoDocumentItemsException(String message) {
        super(message);
    }
}
