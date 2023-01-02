package com.example.store.model.responses;

import lombok.Getter;

import java.util.Calendar;
import java.util.List;

@Getter
public class ShortageResponse {

    private int id;
    private String info;
    private List<ShortageResponseLine> list;
    private Long timestamp;

    public ShortageResponse(int id, String info, List<ShortageResponseLine> list) {
        this.id = id;
        this.info = info;
        this.list = list;
        this.timestamp = Calendar.getInstance().getTimeInMillis();
    }
}
