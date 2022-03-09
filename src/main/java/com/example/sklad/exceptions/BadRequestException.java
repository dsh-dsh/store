package com.example.sklad.exceptions;

import com.example.sklad.utils.Constants;

public class BadRequestException extends RuntimeException {

    public BadRequestException() {
        super(Constants.BAD_REQUEST_MESSAGE);
    }
    public BadRequestException(String message) {
        super(message);
    }

}
