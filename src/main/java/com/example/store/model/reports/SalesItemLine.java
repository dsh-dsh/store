package com.example.store.model.reports;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesItemLine {

    private int itemId;
    private String name;
    private String unit;
    private BigDecimal quantity;
    private BigDecimal amount;

    public SalesItemLine(int itemId, String name, String unit, BigDecimal quantity, BigDecimal amount) {
        this.itemId = itemId;
        this.name = name;
        this.unit = unit;
        this.quantity = quantity;
        this.amount = amount;
    }
}
