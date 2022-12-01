package com.example.store.model.reports;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesReport {
    private List<SalesItemLine> lines;

    public SalesReport(List<SalesItemLine> lines) {
        this.lines = lines;
    }
}
