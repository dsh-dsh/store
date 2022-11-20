package com.example.store.services;

import com.example.store.model.entities.CheckInfo;
import com.example.store.model.entities.Project;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.enums.PaymentType;
import com.example.store.model.reports.PeriodReport;
import com.example.store.model.reports.ReportLine;
import com.example.store.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PeriodReportService {

    public static final String CASH = "наличные";
    public static final String BY_CARD = "по карте";
    public static final String DELIVERY = "доставка";
    @Autowired
    private DocumentService documentService;
    @Autowired
    private MailService mailService;
    @Autowired
    private DocItemService docItemService;
    @Autowired
    private CheckInfoService checkInfoService;
    @Autowired
    private ProjectService projectService;

    @Autowired
    private MailPeriodReportService mailPeriodReportService;

    // todo add tests

    public PeriodReport getPeriodReport(int projectId, long dateStart, long dateEnd) {
        Project project = projectService.getById(projectId);
        LocalDateTime start = Util.getLocalDateTime(dateStart);
        LocalDateTime end = Util.getLocalDateTime(dateEnd).plusDays(1).minusSeconds(1);
        return getReport(project, start, end);
    }

    public PeriodReport getReport(Project project, LocalDateTime dateStart, LocalDateTime dateEnd) {
        List<Document> documents = documentService.getDocumentsByTypesAndProject(
                List.of(DocumentType.CHECK_DOC, DocumentType.CREDIT_ORDER_DOC), project, dateStart, dateEnd);
        Supplier<Stream<OrderDoc>> streamSupplier = () -> documents.stream()
                .filter(doc -> doc.getDocType() == DocumentType.CREDIT_ORDER_DOC)
                .map(OrderDoc.class::cast);
        return new PeriodReport(project.getName(), Util.getDate(dateStart), Util.getDate(dateEnd),
                getReceiptList(documents), getSpendList(streamSupplier.get(), PaymentType.SALARY_PAYMENT),
                getSpendList(streamSupplier.get(), PaymentType.COST_PAYMENT));
    }

    private List<ReportLine> getSpendList(Stream<OrderDoc> orders, PaymentType paymentType) {
        return orders
                .filter(order -> order.getPaymentType() == paymentType)
                .collect(Collectors.toMap(
                        order -> order.getIndividual().getLastName(),
                        order -> BigDecimal.valueOf(order.getAmount()),
                        BigDecimal::add))
                .entrySet().stream()
                .map(entry -> new ReportLine(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public List<ReportLine> getReceiptList(List<Document> documents) {
        Map<String, BigDecimal> sumMap = initMap();
        documents.stream().filter(document -> document.getDocType() == DocumentType.CHECK_DOC)
                .forEach(check -> {
                    BigDecimal checkAmount = BigDecimal.valueOf(docItemService.getItemsAmount((ItemDoc) check))
                            .setScale(2, RoundingMode.HALF_EVEN);
                    sumMap.merge(getReceiptType((ItemDoc) check), checkAmount, BigDecimal::add);
                });
        List<ReportLine> receipts = new ArrayList<>();
        receipts.add(new ReportLine(CASH, sumMap.get(CASH)));
        receipts.add(new ReportLine(BY_CARD, sumMap.get(BY_CARD)));
        receipts.add(new ReportLine(DELIVERY, sumMap.get(DELIVERY)));
        return receipts;
    }

    private String getReceiptType(ItemDoc check) {
        CheckInfo checkInfo = checkInfoService.getCheckInfo(check);
        String type = checkInfo.isPayedByCard() ? BY_CARD : CASH;
        type = checkInfo.isDelivery() ? DELIVERY : type;
        return type;
    }

    private Map<String, BigDecimal> initMap() {
        Map<String, BigDecimal> sumMap = new HashMap<>();
        sumMap.put(CASH, BigDecimal.ZERO);
        sumMap.put(BY_CARD, BigDecimal.ZERO);
        sumMap.put(DELIVERY, BigDecimal.ZERO);
        return sumMap;
    }

}
