package com.example.sklad.controllers;


import com.example.sklad.model.dto.documents.ItemDocDTO;
import com.example.sklad.model.dto.requests.ItemDocRequestDTO;
import com.example.sklad.model.entities.documents.OrderDoc;
import com.example.sklad.model.enums.DocumentType;
import com.example.sklad.model.enums.PaymentType;
import com.example.sklad.services.OrderService;
import com.example.sklad.utils.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
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
public class OrderDocsControllerTest {

    private static final String URL_PREFIX = "/api/v1/orders";
    private static final float AMOUNT = 1500.00f;
    private static final float TAX = 0.00f;
    private static final String SALARY_TYPE_STRING = Constants.SALARY_PAYMENT_TYPE;
    private static final String SALE_TYPE_STRING = Constants.SALE_PAYMENT_TYPE;


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
    void addSalaryOrderTest() throws Exception {

        ItemDocDTO docDTO = testService.setDTOFields();
        docDTO.setIndividual(testService.setIndividualDTO(3));
        docDTO.setSupplier(testService.setCompanyDTO(1));
        testService.setOrderFields(docDTO, SALARY_TYPE_STRING, AMOUNT, TAX);
        ItemDocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX + "/rko")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<OrderDoc> docs = orderService.getDocumentsByType(DocumentType.WITHDRAW_DOC_DOC);
        assertEquals(1, docs.size());

        assertEquals(AMOUNT, docs.get(0).getAmount());
        assertEquals(TAX, docs.get(0).getTax());
        assertEquals(PaymentType.SALARY_PAYMENT, docs.get(0).getPaymentType());
    }



    @Sql(value = "/sql/orders/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addSaleOrderTest() throws Exception {

        ItemDocDTO docDTO = testService.setDTOFields();
        docDTO.setIndividual(testService.setIndividualDTO(4));
        docDTO.setSupplier(testService.setCompanyDTO(1));
        docDTO.setRecipient(testService.setCompanyDTO(1));
        testService.setOrderFields(docDTO, SALE_TYPE_STRING, AMOUNT, TAX);
        ItemDocRequestDTO requestDTO = testService.setDTO(docDTO);

        this.mockMvc.perform(
                        post(URL_PREFIX + "/pko")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(requestDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("ok"));

        List<OrderDoc> docs = orderService.getDocumentsByType(DocumentType.CREDIT_ORDER_DOC);
        assertEquals(1, docs.size());
        assertEquals(4, docs.get(0).getIndividual().getId());
        assertEquals(AMOUNT, docs.get(0).getAmount());
        assertEquals(TAX, docs.get(0).getTax());
        assertEquals(PaymentType.SALE_PAYMENT, docs.get(0).getPaymentType());
    }

}
