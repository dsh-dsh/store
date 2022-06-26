package com.example.store.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
@AutoConfigureMockMvc
class CatalogControllerTest {

    @Autowired
    private ItemTestService itemTestService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String URL_PREFIX = "/api/v1/catalogs";

    @Test
    void getWorkshopsUnauthorizedTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/workshops"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getWorkshopsTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/workshops"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.[2]").exists())
                .andExpect(jsonPath("$.data.[3]").doesNotExist());

    }

    @Test
    void getUnitsUnauthorizedTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/units"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getUnitsTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/units"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.[4]").exists())
                .andExpect(jsonPath("$.data.[5]").doesNotExist());

    }

    @Test
    void getStorageTypesUnauthorizedTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/storage/types"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getStorageTypesTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/storage/types"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.[2]").exists())
                .andExpect(jsonPath("$.data.[3]").doesNotExist());

    }

    @Test
    void getQuantityTypesUnauthorizedTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/quantity/types"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getQuantityTypesTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/quantity/types"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.[2]").exists())
                .andExpect(jsonPath("$.data.[3]").doesNotExist());

    }

    @Test
    void getPaymentTypesUnauthorizedTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/payment/types"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getPaymentTypesTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/payment/types"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.[6]").exists())
                .andExpect(jsonPath("$.data.[7]").doesNotExist());

    }

    @Test
    void getDocumentTypesUnauthorizedTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/document/types"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getDocumentTypesTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/document/types"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.[8]").exists())
                .andExpect(jsonPath("$.data.[9]").doesNotExist());

    }

    @Test
    void getPriceTypesUnauthorizedTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/price/types"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getPriceTypesTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/price/types"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.[1]").exists())
                .andExpect(jsonPath("$.data.[2]").doesNotExist());

    }

    @Test
    void getStoragesUnauthorizedTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/storages"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getStoragesTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/storages"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.[3]").exists())
                .andExpect(jsonPath("$.data.[4]").doesNotExist());
    }


    @Test
    void getProjectsUnauthorizedTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/projects"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getProjectsTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/projects"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.[3]").exists())
                .andExpect(jsonPath("$.data.[4]").doesNotExist());
    }


    @Test
    void getCompaniesUnauthorizedTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/companies"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getCompaniesTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/companies"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.[2]").exists())
                .andExpect(jsonPath("$.data.[3]").doesNotExist());
    }

    @Test
    void getUsersUnauthorizedTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/users"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getUsersTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.[3]").exists())
                .andExpect(jsonPath("$.data.[4]").doesNotExist());
    }

    @Test
    void getPersonsUnauthorizedTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/persons"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getPersonsTest() throws Exception{
        this.mockMvc.perform(
                        get(URL_PREFIX + "/persons"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.[3]").exists())
                .andExpect(jsonPath("$.data.[4]").doesNotExist());
    }

}
