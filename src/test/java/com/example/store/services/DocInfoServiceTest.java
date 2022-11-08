package com.example.store.services;

import com.example.store.model.dto.DocInfoDTO;
import com.example.store.model.entities.DocInfo;
import com.example.store.model.entities.documents.Document;
import com.example.store.repositories.DocInfoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class DocInfoServiceTest {

    @Autowired
    private DocInfoService docInfoService;
    @Autowired
    private DocInfoRepository docInfoRepository;
    @Autowired
    private DocumentService documentService;


    @Sql(value = "/sql/docInfo/addDocInfo.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/docInfo/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getDocInfoByDocumentTest() {
        Document document = documentService.getDocumentById(1);
        DocInfo docInfo = docInfoService.getDocInfoByDocument(document);
        assertEquals("comment", docInfo.getComment());
        assertEquals("SUP_NUM 123456789", docInfo.getSupplierDocNumber());
        assertEquals( document, docInfo.getDocument());
    }

    @Sql(value = "/sql/docInfo/addDocInfo.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/docInfo/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getDocInfoDTOByDocumentTest() {
        Document document = documentService.getDocumentById(1);
        DocInfoDTO dto = docInfoService.getDocInfoDTOByDocument(document);
        assertEquals("comment", dto.getComment());
        assertEquals("SUP_NUM 123456789", dto.getSupplierDocNumber());
    }

    @Sql(value = "/sql/docInfo/addDocInfo.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/docInfo/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setDocInfoNewTest() {
        Document document = documentService.getDocumentById(2);
        DocInfoDTO dto = new DocInfoDTO();
        dto.setComment("new comment");
        dto.setSupplierDocNumber("new supplier doc number");
        docInfoService.setDocInfo(document, dto);
        DocInfo docInfo = docInfoService.getDocInfoByDocument(document);
        assertEquals("new comment", docInfo.getComment());
        assertEquals("new supplier doc number", docInfo.getSupplierDocNumber());
        assertEquals(document, docInfo.getDocument());
    }

    @Sql(value = "/sql/docInfo/addDocInfo.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/docInfo/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void setDocInfoUpdateTest() {
        Document document = documentService.getDocumentById(1);
        DocInfoDTO dto = new DocInfoDTO();
        dto.setId(1);
        dto.setComment("update comment");
        dto.setSupplierDocNumber("update supplier doc number");
        docInfoService.setDocInfo(document, dto);
        DocInfo docInfo = docInfoRepository.getById(1);
        assertEquals("update comment", docInfo.getComment());
        assertEquals("update supplier doc number", docInfo.getSupplierDocNumber());
        assertEquals(document, docInfo.getDocument());
    }
}