package com.example.store.model.reports;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MoveDocLine {

    private int id;
    private String date;
    private String name;
    private String supplier;
    private String storageTo;
    private BigDecimal quantity;
    private boolean isHold;

    public MoveDocLine(int id, String date, String name,
                       String supplier, String storageTo,
                       BigDecimal quantity, boolean isHold) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.supplier = supplier;
        this.storageTo = storageTo;
        this.quantity = quantity;
        this.isHold = isHold;
    }
}
