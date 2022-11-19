package com.example.store.model.reports;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@ToString
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemLine {
    private String name;
    private BigDecimal startRest;
    private BigDecimal receipt;
    private BigDecimal expense;
    private BigDecimal endRest;
    private List<MoveDocLine> docs;

    public ItemLine(String name, BigDecimal startRest,
                    BigDecimal receipt, BigDecimal expense,
                    BigDecimal endRest, List<MoveDocLine> docs) {
        this.name = name;
        this.startRest = startRest;
        this.receipt = receipt;
        this.expense = expense;
        this.endRest = endRest;
        this.docs = docs;
    }
}
