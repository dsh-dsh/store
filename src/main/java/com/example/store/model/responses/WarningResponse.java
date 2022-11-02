package com.example.store.model.responses;

import lombok.Getter;

import java.util.Calendar;

@Getter
public class WarningResponse {

    private int type;
    private String warning;
    private Long timestamp;

    public WarningResponse(String warning) {
        this.warning = warning;
        this.timestamp = Calendar.getInstance().getTimeInMillis();
    }

    public WarningResponse(String warning, int type) {
        this.type = type;
        this.warning = warning;
        this.timestamp = Calendar.getInstance().getTimeInMillis();
    }

}
