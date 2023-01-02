package com.example.store.model.responses;

import java.util.List;

public class Warning {

    String message;
    List<String> info;

    public String getMessage() {
        return message;
    }

    public List<String> getInfo() {
        return info;
    }
}