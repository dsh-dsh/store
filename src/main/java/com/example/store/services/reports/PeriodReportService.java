package com.example.store.services.reports;

import com.example.store.model.entities.CheckInfo;
import com.example.store.model.entities.Project;
import com.example.store.model.entities.Storage;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.CheckPaymentType;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.enums.PaymentType;
import com.example.store.model.reports.PeriodReport;
import com.example.store.model.reports.ReportLine;
import com.example.store.services.*;
import com.example.store.utils.Util;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PeriodReportService {

    public static final String CASH = CheckPaymentType.CASH_PAYMENT.getValue();
    public static final String BY_CARD = CheckPaymentType.CARD_PAYMENT.getValue();
    public static final String BY_QR = CheckPaymentType.QR_PAYMENT.getValue();
    public static final String DELIVERY = CheckPaymentType.DELIVERY_PAYMENT.getValue();

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
    private StorageService storageService;

    @Autowired
    private MailPeriodReportService mailPeriodReportService;

    public PeriodReport getPeriodReport(int projectId, long dateStart, long dateEnd) {
        Project project = projectService.getById(projectId);
        LocalDateTime start = Util.getLocalDate(dateStart).atStartOfDay();
        LocalDateTime end = Util.getLocalDate(dateEnd).atStartOfDay().plusDays(1);
        return getReport(project, start, end);
    }

    public PeriodReport getReport(Project project, LocalDateTime dateStart, LocalDateTime dateEnd) {
        List<Document> documents = documentService.getDocumentsByTypesAndProject(
                List.of(DocumentType.CHECK_DOC, DocumentType.CREDIT_ORDER_DOC), project, dateStart, dateEnd);
        Supplier<Stream<OrderDoc>> streamSupplier = () -> documents.stream()
                .filter(doc -> doc.getDocType() == DocumentType.CREDIT_ORDER_DOC)
                .map(OrderDoc.class::cast);
        return new PeriodReport(project.getName(), Util.getDate(dateStart), Util.getDate(dateEnd), getIncomingAmount(project, dateStart, dateEnd),
                getReceiptList(documents), getSpendList(streamSupplier.get(), PaymentType.SALARY_PAYMENT),
                getSpendList(streamSupplier.get(), PaymentType.COST_PAYMENT));
    }

    // todo add tests
    protected BigDecimal getIncomingAmount(Project project, LocalDateTime from, LocalDateTime to) {
        List<DocumentType> types = List.of(DocumentType.POSTING_DOC, DocumentType.MOVEMENT_DOC);
        Storage storage = storageService.getByName(project.getName());
        List<ItemDoc> documents = documentService.getDocumentsByTypeAndStorageAndIsHold(types, storage, true, from, to);
        return documents.stream()
                .map(docItemService::getItemsAmount)
                .map(BigDecimal::valueOf)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    protected List<ReportLine> getSpendList(Stream<OrderDoc> orders, PaymentType paymentType) {
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
        return documents.stream().filter(document -> document.getDocType() == DocumentType.CHECK_DOC)
                .collect(Collectors.toMap(this::getReceiptType, this::getCheckAmount, BigDecimal::add))
                .entrySet().stream()
                .map(entry -> new ReportLine(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @NotNull
    private BigDecimal getCheckAmount(Document check) {
        return BigDecimal.valueOf(docItemService.getItemsAmount((ItemDoc) check))
                .setScale(2, RoundingMode.HALF_EVEN);
    }

    protected String getReceiptType(Document check) {
        CheckInfo checkInfo = checkInfoService.getCheckInfo((ItemDoc) check);
        return checkInfo.getCheckPaymentType().getValue();
    }

}
