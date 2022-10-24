package com.example.store.model.responses;

import lombok.Getter;

import java.util.Calendar;

@Getter
public class WarningResponse {

    private String warning;
    private Long timestamp;

    public WarningResponse(String warning) {
        this.warning = warning;
        this.timestamp = Calendar.getInstance().getTimeInMillis();
    }

}
