package com.example.store.services;

import com.example.store.model.entities.CheckInfo;
import com.example.store.model.entities.Project;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.CheckPaymentType;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.enums.PaymentType;
import com.example.store.model.reports.ReportLine;
import com.example.store.utils.Util;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class PeriodReportServiceTest {

    @Autowired
    private PeriodReportService periodReportService;
    @Autowired
    private DocumentService documentService;
    @Autowired
    private ProjectService projectService;

    @Mock
    private ItemDoc mockedCheck;
    @Mock
    private CheckInfo mockedCheckInfo;
    @Mock
    private CheckInfoService mockedCheckInfoService;
    @InjectMocks
    private PeriodReportService injectedPeriodReportService;

    public static final String CASH = CheckPaymentType.CASH_PAYMENT.getValue();
    public static final String BY_CARD = CheckPaymentType.CARD_PAYMENT.getValue();
    public static final String DELIVERY = CheckPaymentType.DELIVERY_PAYMENT.getValue();

    @Test
    void getPeriodReportTest() {
        PeriodReportService spyReportService = spy(periodReportService);
        Project project = projectService.getById(3);
        long start = Util.getLongLocalDateTime("15.04.22 00:00:00");
        long end = Util.getLongLocalDateTime("16.04.22 00:00:00");
        spyReportService.getPeriodReport(3, start, end);
        verify(spyReportService, times(1))
                .getReport(eq(project), eq(Util.getLocalDateTime(start)),
                        eq(Util.getLocalDate(end).atStartOfDay().plusDays(1)));
    }

    @Test
    void getReportTest() {
        Project project = projectService.getById(3);
        LocalDateTime start = Util.getLocalDateTime("15.04.22 00:00:00");
        LocalDateTime end = Util.getLocalDateTime("16.04.22 00:00:00").plusDays(1).minusSeconds(1);
        PeriodReportService spyReportService = spy(periodReportService);
        spyReportService.getReport(project, start, end);
        verify(spyReportService, times(1)).getReceiptList(any());
        verify(spyReportService, times(1)).getSpendList(any(), eq(PaymentType.SALARY_PAYMENT));
        verify(spyReportService, times(1)).getSpendList(any(), eq(PaymentType.COST_PAYMENT));
    }

    @Sql(value = {"/sql/documents/addTenChecks.sql", "/sql/documents/add5OrderDocs.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getCostSpendListTest() {
        List<Document> documents = documentService.getAllDocuments();
        Stream<OrderDoc> docStream = documents.stream()
                .filter(doc -> doc.getDocType() == DocumentType.CREDIT_ORDER_DOC)
                .map(OrderDoc.class::cast);
        List<ReportLine> reportLines = periodReportService.getSpendList(docStream, PaymentType.COST_PAYMENT);
        assertEquals(1, reportLines.size());
        assertEquals("Иванов", reportLines.get(0).getName());
        assertEquals(0, BigDecimal.valueOf(1000).compareTo(reportLines.get(0).getValue()));
    }

    @Sql(value = {"/sql/documents/addTenChecks.sql", "/sql/documents/add5OrderDocs.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getSalarySpendListTest() {
        List<Document> documents = documentService.getAllDocuments();
        Stream<OrderDoc> docStream = documents.stream()
                .filter(doc -> doc.getDocType() == DocumentType.CREDIT_ORDER_DOC)
                .map(OrderDoc.class::cast);
        List<ReportLine> reportLines = periodReportService.getSpendList(docStream, PaymentType.SALARY_PAYMENT);
        assertEquals(3, reportLines.size());
        assertEquals("Сидоров", reportLines.get(0).getName());
        assertEquals(0, BigDecimal.valueOf(1200).compareTo(reportLines.get(0).getValue()));
        assertEquals("Олегова", reportLines.get(1).getName());
        assertEquals(0, BigDecimal.valueOf(2500).compareTo(reportLines.get(1).getValue()));
        assertEquals("Васильев", reportLines.get(2).getName());
        assertEquals(0, BigDecimal.valueOf(1300).compareTo(reportLines.get(2).getValue()));
    }

    @Sql(value = {"/sql/documents/addTenChecks.sql", "/sql/documents/add5OrderDocs.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getReceiptListTest() {
        List<Document> documents = documentService.getAllDocuments();
        List<ReportLine> reportLines = periodReportService.getReceiptList(documents);
        assertEquals(4, reportLines.size());
        assertEquals(CheckPaymentType.QR_PAYMENT.getValue(), reportLines.get(0).getName());
        assertEquals(0, BigDecimal.valueOf(450).compareTo(reportLines.get(0).getValue()));
        assertEquals(CheckPaymentType.CARD_PAYMENT.getValue(), reportLines.get(1).getName());
        assertEquals(0, BigDecimal.valueOf(790).compareTo(reportLines.get(1).getValue()));
        assertEquals(CheckPaymentType.CASH_PAYMENT.getValue(), reportLines.get(2).getName());
        assertEquals(0, BigDecimal.valueOf(480).compareTo(reportLines.get(2).getValue()));
        assertEquals(CheckPaymentType.DELIVERY_PAYMENT.getValue(), reportLines.get(3).getName());
        assertEquals(0, BigDecimal.valueOf(440).compareTo(reportLines.get(3).getValue()));
    }

    @Test
    void getReceiptTypeDeliveryTest() {
        when(mockedCheckInfo.getCheckPaymentType()).thenReturn(CheckPaymentType.DELIVERY_PAYMENT);
        when(mockedCheckInfoService.getCheckInfo(any())).thenReturn(mockedCheckInfo);
        assertEquals(DELIVERY, injectedPeriodReportService.getReceiptType(mockedCheck));
    }

    @Test
    void getReceiptTypeCashTest() {
        when(mockedCheckInfo.getCheckPaymentType()).thenReturn(CheckPaymentType.CASH_PAYMENT);
        when(mockedCheckInfoService.getCheckInfo(any())).thenReturn(mockedCheckInfo);
        assertEquals(CASH, injectedPeriodReportService.getReceiptType(mockedCheck));
    }

    @Test
    void getReceiptTypeCardTest() {
        when(mockedCheckInfo.getCheckPaymentType()).thenReturn(CheckPaymentType.CARD_PAYMENT);
        when(mockedCheckInfoService.getCheckInfo(any())).thenReturn(mockedCheckInfo);
        assertEquals(BY_CARD, injectedPeriodReportService.getReceiptType(mockedCheck));
    }

    private List<Document> getDocuments() {
        List<Document> documents = new ArrayList<>();
        documents.add(getDocument());
        return documents;
    }

    private Document getDocument(){
        Document document = new Document();
        return document;
    }
}
