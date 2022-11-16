package com.example.store.model.reports;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReportLine {

    private String name;
    private BigDecimal value;

    public  ReportLine(String name, BigDecimal value) {
        this.name = name;
        this.value = value;
    }

}
