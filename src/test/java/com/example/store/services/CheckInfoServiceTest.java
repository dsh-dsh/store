package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.model.dto.CheckInfoDTO;
import com.example.store.model.entities.CheckInfo;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.entities.documents.ItemDoc;
import com.example.store.model.enums.CheckPaymentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class CheckInfoServiceTest {

    @Autowired
    private CheckInfoService checkInfoService;
    @Autowired
    private DocumentService documentService;

    private static final long DATE = 1647428400000L; // 03.16.2022 14:00:00

    @Sql(value = "/sql/documents/addCheckDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getCheckInfoTest() {
        Document document = documentService.getDocumentById(1);
        CheckInfo checkInfo = checkInfoService.getCheckInfo((ItemDoc) document);
        assertNotNull(checkInfo);
        assertEquals(1, checkInfo.getCheck().getId());
    }

    @Sql(value = "/sql/documents/addCheckDocAndDocItemsOnly.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addCheckInfoTest() {
        Document document = documentService.getDocumentById(7);
        CheckInfoDTO dto = getInfoDTO();
        checkInfoService.addCheckInfo(dto, (ItemDoc) document);
        CheckInfo checkInfo = checkInfoService.getCheckInfo((ItemDoc) document);
        assertNotNull(checkInfo);
        assertEquals(7, checkInfo.getCheck().getId());
    }

    @Sql(value = "/sql/documents/addCheckDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updateCheckInfoTest() {
        Document document = documentService.getDocumentById(1);
        CheckInfoDTO dto = getInfoDTO();
        checkInfoService.updateCheckInfo(dto, (ItemDoc) document);
        CheckInfo checkInfo = checkInfoService.getCheckInfo((ItemDoc) document);
        assertNotNull(checkInfo);
        assertEquals("abc", checkInfo.getWaiter());
    }

    @Test
    void setFieldsTest() {
        CheckInfo checkInfo = new CheckInfo();
        CheckInfoDTO dto = getInfoDTO();
        checkInfoService.setFields(dto, checkInfo);
        assertEquals(12345, checkInfo.getCheckNumber());
        assertEquals(CheckPaymentType.QR_PAYMENT, checkInfo.getCheckPaymentType());
    }

    @Sql(value = "/sql/documents/addCheckDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void deleteByDocIdTest() {
        Document document = documentService.getDocumentById(1);
        checkInfoService.deleteByDoc((ItemDoc) document);
        assertThrows(BadRequestException.class, () -> checkInfoService.getCheckInfo((ItemDoc) document));
    }

    @Sql(value = "/sql/documents/addCheckDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void countRowsByDocTest() {
        Document document = documentService.getDocumentById(1);
        assertEquals(1, checkInfoService.countRowsByDoc(document.getId()));
    }

    @Sql(value = "/sql/documents/addCheckDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getCheckInfoDTOTest() {
        Document document = documentService.getDocumentById(1);
        CheckInfoDTO dto = checkInfoService.getCheckInfoDTO((ItemDoc) document);
        assertEquals("Официант 10", dto.getWaiter());
    }

    @Sql(value = "/sql/documents/addCheckDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/documents/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getCheckPaymentTypeTest() {
        Document document = documentService.getDocumentById(1);
        assertEquals(CheckPaymentType.CARD_PAYMENT, checkInfoService.getCheckPaymentType((ItemDoc) document));
    }

    private CheckInfoDTO getInfoDTO() {
        CheckInfoDTO dto = new CheckInfoDTO();
        dto.setAmountReceived(1000.00f);
        dto.setCashRegisterNumber(123456789L);
        dto.setCheckNumber(12345);
        dto.setDateTime(DATE);
        dto.setGuestNumber(1);
        dto.setDelivery(false);
        dto.setKKMChecked(true);
        dto.setPayed(true);
        dto.setCheckPaymentType(CheckPaymentType.QR_PAYMENT.getValue());
        dto.setReturn(false);
        dto.setTableNumber(1);
        dto.setWaiter("abc");
        return dto;
    }
}