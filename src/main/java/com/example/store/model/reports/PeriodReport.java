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
public class PeriodReport {

    private String department;
    private String dateStart;
    private String dateEnd;
    private List<ReportLine> receipts;
    private List<ReportLine> salary;
    private List<ReportLine> spends;

    public PeriodReport(String department, String dateStart, String dateEnd,
                  List<ReportLine> receipts, List<ReportLine> salary, List<ReportLine> spends) {
        this.department = department;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.receipts = receipts;
        this.salary = salary;
        this.spends = spends;
    }
}
