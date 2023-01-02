package com.example.store.exceptions;

import com.example.store.model.responses.ShortageResponseLine;
import lombok.Getter;

import java.util.List;

@Getter
public class ShortageException extends RuntimeException {

    private List<ShortageResponseLine> list;
    private int docId;
    private String info;

    public ShortageException(String message, String info) {
        super(message);
        this.info = info;
    }

    public ShortageException(String message, List<ShortageResponseLine> list, int docId, String info) {
        super(message);
        this.list = list;
        this.docId = docId;
        this.info = info;

    }
}
