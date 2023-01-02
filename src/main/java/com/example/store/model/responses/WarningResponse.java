package com.example.store.model.responses;

import lombok.Getter;

import java.util.Calendar;
import java.util.List;

@Getter
public class WarningResponse {

    private int type;
    private String warning;
    private List<ShortageResponseLine> info;
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

    public WarningResponse(String warning, List<ShortageResponseLine> info, int type) {
        this.type = type;
        this.warning = warning;
        this.info = info;
        this.timestamp = Calendar.getInstance().getTimeInMillis();
    }

}
