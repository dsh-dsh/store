package com.example.store.services;

import com.example.store.model.entities.Project;
import com.example.store.model.reports.PeriodReport;
import com.example.store.model.reports.ReportLine;
import com.example.store.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MailPeriodReportService {

    public static final String TOTAL = "ИТОГО:";
    private static final String HTML_UNDER_LINE = "<div class=\"width-content\"><hr></div>";

    @Autowired
    private PeriodReportService periodReportService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private MailService mailService;

    @Value("${report.to.emails}")
    private String[] emails;


    // todo add tests

    public void sendPeriodReport(String projectName, long date) {
        Project project = projectService.getByName(projectName);
        LocalDateTime start = Util.getLocalDate(date).atStartOfDay();
        String report = reportToHTML(periodReportService.getReport(project, start, start.plusDays(1)));
        String subject = "Отчет по кафе " + projectName + " за " + Util.getDate(start);
        mailService.prepareAndSend(subject, report, emails);
    }

    public String reportToHTML(PeriodReport report) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"ru\">\n" +
                "<head>\n" +
                "<meta charset=\"Windows-1251\">\n" +
                "<title>Title</title>\n" +
                "<style type=\"text/css\">" +
                "    body {" +
                "       font-family: Verdana, Arial, Helvetica, sans-serif;" +
                "       color: #333366;}" +
                "    table {" +
                "       table-layout: fixed;}" +
                "    hr {" +
                "       max-width: 350px;}" +
                "    h4 {" +
                "       margin-top: 10px;" +
                "       margin-bottom: 4px;}" +
                "    .width-content {" +
                "       max-width: 350px;}" +
                "    .right-align {" +
                "       text-align: end;}" +
                "</style>\n" +
                "</head>\n" +
                "<body>" +
                "<h3>Отчет по кафе " + report.getDepartment() + " за " + report.getDateStart() + "</h3>" +
                "<h4>выручка</h4>" +
                listToHTML(report.getReceipts()) + HTML_UNDER_LINE +
                getHTMLWithGap(TOTAL, getAmountOfList(report.getReceipts()).toString()) +
                "<h4>зарплата</h4>" +
                listToHTML(report.getSalary()) + HTML_UNDER_LINE +
                getHTMLWithGap(TOTAL, getAmountOfList(report.getSalary()).toString()) +
                "<h4>расходы</h4>" +
                listToHTML(report.getSpends()) + HTML_UNDER_LINE +
                getHTMLWithGap(TOTAL, getAmountOfList(report.getSpends()).toString()) +
                getHTMLWithGap("ОСТАТОК НАЛИЧНЫХ:", getTotalAmount(report).toString()) +
                "</body></html>";
    }

    protected String listToHTML(List<ReportLine> list) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<table width=\"350px\">");
        for(ReportLine line : list) {
            stringBuilder.append(lineToHTML(line));
        }
        stringBuilder.append("</table>");
        return stringBuilder.toString();
    }

    protected String lineToHTML(ReportLine line) {
        return "<tr><td>" + line.getName() + "</td><td></td><td class=\"right-align\">" + line.getValue().toString() + "</td></tr>";
    }

    protected String getHTMLWithGap(String leftValue, String rightValue) {
        return "<table width=\"350px\">" +
                "<tr><td>" + leftValue + "</td>" +
                "<td></td>" +
                "<td class=\"right-align\">" + rightValue + "</td></tr>" +
                "</table>";
    }

    protected BigDecimal getTotalAmount(PeriodReport report) {
        return report.getReceipts().get(0).getValue()
                .subtract(getAmountOfList(report.getSalary()).add(getAmountOfList(report.getSpends())));
    }

    protected BigDecimal getAmountOfList(List<ReportLine> list) {
        return list.stream().map(ReportLine::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
