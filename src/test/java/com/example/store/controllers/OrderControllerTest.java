package com.example.store.controllers;


import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.dto.requests.DocRequestDTO;
import com.example.store.model.entities.documents.OrderDoc;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.enums.PaymentType;
import com.example.store.services.OrderService;
import com.example.store.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    private static final String URL_PREFIX = "/api/v1/orders";
    private static final float AMOUNT = 1500.00f;
    private static final float TAX = 0.00f;
    private static final String SALARY_TYPE_STRING = Constants.SALARY_PAYMENT_TYPE;
    private static final String SALE_TYPE_STRING = Constants.SALE_CASH_PAYMENT_TYPE;
    private static final String SUPPLIER_TYPE_STRING = Constants.SUPPLIER_PAYMENT_TYPE;
    private static final String OTHER_PAYMENT_STRING = Constants.OTHER_PAYMENT_TYPE;
    private static final int INDIVIDUAL_ID = 3;
    private static final int SUPPLIER_ID = 1;
    private static final int RECIPIENT_ID = 2;

    @Autowired
    private TestService testService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OrderService orderService;

    @Sql(value = "/sql/orders/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void addSalaryOrderTest() throws Exception {
        DocDTO docDTO = testService.setDTOFields(DocumentType.WITHDRAW_ORDER_DOC);
        docDTO.setIndividual(testService.setIndividualDTO(INDIVIDUAL_ID));
        docDTO.setSupplier(testService.setCompanyDTO(SUPPLIER_ID));
        testService.setOrderFields(docDTO, SALARY_TYPE_STRING, AMOUNT, TAX);
        DocRequestDTO requestDTO = testService.setDTO(docDTO);
        this.mockMvc.perform(
                        post(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<OrderDoc> docs = orderService.getDocumentsByType(DocumentType.WITHDRAW_ORDER_DOC);
        assertEquals(TestService.ONE_DOCUMENT, docs.size());
        assertEquals(AMOUNT, docs.get(0).getAmount());
        assertEquals(TAX, docs.get(0).getTax());
        assertEquals(PaymentType.SALARY_PAYMENT, docs.get(0).getPaymentType());
    }

    @Test
    void addOrderUnauthorizedTest() throws Exception {
        DocDTO docDTO = testService.setDTOFields(DocumentType.WITHDRAW_ORDER_DOC);
        DocRequestDTO requestDTO = testService.setDTO(docDTO);
        this.mockMvc.perform(
                        post(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/orders/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void addSaleOrderTest() throws Exception {
        DocDTO docDTO = testService.setDTOFields(DocumentType.CREDIT_ORDER_DOC);
        docDTO.setIndividual(testService.setIndividualDTO(INDIVIDUAL_ID));
        docDTO.setSupplier(testService.setCompanyDTO(SUPPLIER_ID));
        docDTO.setRecipient(testService.setCompanyDTO(RECIPIENT_ID));
        testService.setOrderFields(docDTO, SALE_TYPE_STRING, AMOUNT, TAX);
        DocRequestDTO requestDTO = testService.setDTO(docDTO);
        this.mockMvc.perform(
                        post(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<OrderDoc> docs = orderService.getDocumentsByType(DocumentType.CREDIT_ORDER_DOC);
        assertEquals(TestService.ONE_DOCUMENT, docs.size());
        assertEquals(INDIVIDUAL_ID, docs.get(0).getIndividual().getId());
        assertEquals(AMOUNT, docs.get(0).getAmount());
        assertEquals(TAX, docs.get(0).getTax());
        assertEquals(PaymentType.SALE_CASH_PAYMENT, docs.get(0).getPaymentType());
    }

    @Test
    void updateOrderDocUnauthorizedTest() throws Exception {
        DocDTO docDTO = testService.setDTOFields(DocumentType.WITHDRAW_ORDER_DOC);
        DocRequestDTO requestDTO = testService.setDTO(docDTO);
        this.mockMvc.perform(
                        put(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/orders/addWithdrawDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/orders/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void updateWithdrawDocTest() throws Exception {
        DocDTO docDTO = testService.setDTOFields(DocumentType.WITHDRAW_ORDER_DOC);
        testService.addTo(docDTO, 2, TestService.DOC_NUMBER);
        docDTO.setIndividual(testService.setIndividualDTO(INDIVIDUAL_ID));
        docDTO.setSupplier(testService.setCompanyDTO(SUPPLIER_ID));
        testService.setOrderFields(docDTO, SUPPLIER_TYPE_STRING, AMOUNT, TAX);
        DocRequestDTO requestDTO = testService.setDTO(docDTO);
        this.mockMvc.perform(
                        put(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<OrderDoc> docs = orderService.getDocumentsByType(DocumentType.WITHDRAW_ORDER_DOC);
        assertEquals(TestService.ONE_DOCUMENT, docs.size());
        assertEquals(SUPPLIER_ID, docs.get(0).getSupplier().getId());
        assertEquals(AMOUNT, docs.get(0).getAmount());
        assertEquals(TAX, docs.get(0).getTax());
        assertEquals(PaymentType.SUPPLIER_PAYMENT, docs.get(0).getPaymentType());
    }

    @Sql(value = "/sql/orders/addCreditDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/orders/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void updateCreditDocTest() throws Exception {
        DocDTO docDTO = testService.setDTOFields(DocumentType.CREDIT_ORDER_DOC);
        testService.addTo(docDTO, TestService.DOC_ID, TestService.DOC_NUMBER);
        docDTO.setIndividual(testService.setIndividualDTO(INDIVIDUAL_ID));
        docDTO.setSupplier(testService.setCompanyDTO(SUPPLIER_ID));
        docDTO.setRecipient(testService.setCompanyDTO(RECIPIENT_ID));
        testService.setOrderFields(docDTO, OTHER_PAYMENT_STRING, AMOUNT, TAX);
        DocRequestDTO requestDTO = testService.setDTO(docDTO);
        this.mockMvc.perform(
                        put(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<OrderDoc> docs = orderService.getDocumentsByType(DocumentType.CREDIT_ORDER_DOC);
        assertEquals(TestService.ONE_DOCUMENT, docs.size());
        assertEquals(TestService.DOC_NUMBER, docs.get(0).getNumber());
        assertEquals(SUPPLIER_ID, docs.get(0).getSupplier().getId());
        assertEquals(RECIPIENT_ID, docs.get(0).getRecipient().getId());
        assertEquals(AMOUNT, docs.get(0).getAmount());
        assertEquals(TAX, docs.get(0).getTax());
        assertEquals(PaymentType.OTHER_PAYMENT, docs.get(0).getPaymentType());
    }

    @Sql(value = "/sql/orders/addCreditDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/orders/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void softDeleteCreditDocTest() throws Exception {
        DocDTO docDTO = testService.setDTOFields(DocumentType.CREDIT_ORDER_DOC);
        testService.addTo(docDTO, TestService.DOC_ID, TestService.DOC_NUMBER);
        DocRequestDTO requestDTO = testService.setDTO(docDTO);
        this.mockMvc.perform(
                        delete(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<OrderDoc> docs = orderService.getDocumentsByType(DocumentType.CREDIT_ORDER_DOC);
        assertEquals(TestService.ONE_DOCUMENT, docs.size());
        assertTrue(docs.get(0).isDeleted());
    }

    @Sql(value = "/sql/orders/addWithdrawDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/orders/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void softDeleteWithdrawDocTest() throws Exception {
        DocDTO docDTO = testService.setDTOFields(DocumentType.CREDIT_ORDER_DOC);
        testService.addTo(docDTO, 2, TestService.DOC_NUMBER);
        DocRequestDTO requestDTO = testService.setDTO(docDTO);
        this.mockMvc.perform(
                        delete(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<OrderDoc> docs = orderService.getDocumentsByType(DocumentType.WITHDRAW_ORDER_DOC);
        assertEquals(TestService.ONE_DOCUMENT, docs.size());
        assertTrue(docs.get(0).isDeleted());
    }

    @Test
    void softDeleteUnauthorizedTest() throws Exception {
        DocDTO docDTO = testService.setDTOFields(DocumentType.CREDIT_ORDER_DOC);
        DocRequestDTO requestDTO = testService.setDTO(docDTO);
        this.mockMvc.perform(
                        delete(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/orders/addWithdrawDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/orders/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getWithdrawDocTest() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX).param("id", String.valueOf(2)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.doc_type").value(DocumentType.WITHDRAW_ORDER_DOC.toString()))
                .andExpect(jsonPath("$.data.payment_type").value(PaymentType.SALARY_PAYMENT.toString()));
    }

    @Sql(value = "/sql/orders/addCreditDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/orders/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getCreditDocTest() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX).param("id", String.valueOf(TestService.DOC_ID)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(TestService.DOC_ID))
                .andExpect(jsonPath("$.data.doc_type").value(DocumentType.CREDIT_ORDER_DOC.toString()))
                .andExpect(jsonPath("$.data.payment_type").value(PaymentType.SALE_CASH_PAYMENT.toString()));
    }

    @Test
    void getOrderDocUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX).param("id", String.valueOf(TestService.DOC_ID)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

}
