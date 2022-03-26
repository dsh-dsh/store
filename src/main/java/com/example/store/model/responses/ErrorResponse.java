package com.example.store.model.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Calendar;

@Data
public class ErrorResponse {
    private String error;
    @JsonProperty("error_description")
    private String errorDesc;
    private Long timestamp;

    public ErrorResponse(String error) {
        this.error = error;
        this.errorDesc = error;
        this.timestamp = Calendar.getInstance().getTimeInMillis();
    }
}
