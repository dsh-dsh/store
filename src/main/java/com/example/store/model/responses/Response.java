package com.example.store.model.responses;

import lombok.Data;

@Data
public class Response<T> {

    private T data;
    private String message;

    public Response(T data) {
        this.data = data;
    }

    public Response(T data, String message) {
        this.data = data;
        this.message = message;
    }
}
