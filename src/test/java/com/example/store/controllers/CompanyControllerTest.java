package com.example.store.controllers;

import com.example.store.model.dto.CompanyDTO;
import com.example.store.model.entities.Company;
import com.example.store.services.CompanyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CompanyService companyService;

    private static final String URL_PREFIX = "/api/v1/companies";

    @Sql(value = "/sql/company/addCompaniesForTree.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/company/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getItemTreeTest() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/tree"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.[4]").exists())
                .andExpect(jsonPath("$.data.[5]").doesNotExist())
                .andExpect(jsonPath("$.data.[2].children.[0]").doesNotExist())
                .andExpect(jsonPath("$.data.[3].children.[0]").exists())
                .andExpect(jsonPath("$.data.[4].children.[0]").exists());

    }

    @Sql(value = "/sql/company/addCompaniesForTree.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/company/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getItemTreeUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/tree"))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }

    @Sql(value = "/sql/company/addCompaniesForTree.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/company/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getCompanyTest() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX)
                                .param("id", String.valueOf(6)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Company name 1"))
                .andExpect(jsonPath("$.data.code").value(125));
    }


    @Sql(value = "/sql/company/addCompaniesForTree.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/company/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getCompanyUnauthorizedTest() throws Exception {
        String date = String.valueOf(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        this.mockMvc.perform(
                        get(URL_PREFIX)
                                .param("id", String.valueOf(3))
                                .param("date", date))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/company/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void setCompanyTest() throws Exception {
        CompanyDTO dto = getCompanyDTO(0, 123);
        this.mockMvc.perform(
                        post(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Sql(value = "/sql/company/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setCompanyUnauthorizedTest() throws Exception {
        CompanyDTO dto = getCompanyDTO(0, 123);
        this.mockMvc.perform(
                        post(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/company/addCompaniesForTree.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/company/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void updateCompanyTest() throws Exception {
        CompanyDTO dto = getCompanyDTO(7, 1256);
        this.mockMvc.perform(
                        put(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk());
        Company company = companyService.getById(7);
        assertEquals("316316241412", company.getInn());
        assertEquals(1256, company.getCode());
    }

    @Sql(value = "/sql/company/addCompaniesForTree.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/company/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void updateCompanyWithWrongIdTest() throws Exception {
        CompanyDTO dto = getCompanyDTO(8, 1256);
        this.mockMvc.perform(
                        put(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Sql(value = "/sql/company/addCompaniesForTree.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/company/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updateCompanyUnauthorizedTest() throws Exception {
        CompanyDTO dto = getCompanyDTO(8, 1256);
        this.mockMvc.perform(
                        put(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @NotNull
    private CompanyDTO getCompanyDTO(int id, int code) {
        CompanyDTO dto = new CompanyDTO();
        dto.setId(id);
        dto.setName("new company name");
        dto.setInn("316316241412");
        dto.setCode(code);
        return dto;
    }
}
