package com.example.store.exceptions;

import com.example.store.utils.Constants;

public class BadRequestException extends RuntimeException {

    public BadRequestException() {
        super(Constants.BAD_REQUEST_MESSAGE);
    }
    public BadRequestException(String message) {
        super(message);
    }

}
