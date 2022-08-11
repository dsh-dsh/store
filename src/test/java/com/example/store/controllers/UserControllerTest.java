package com.example.store.controllers;

import com.example.store.model.dto.PersonDTO;
import com.example.store.model.entities.User;
import com.example.store.model.enums.Role;
import com.example.store.services.UserService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    private static final String URL_PREFIX = "/api/v1/users";
    private static final int PERSON_ID = 1;
    private static final String PERSON_EMAIL = "customer@mail.ru";

    private static final int USER_ID = 1;
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Smiths";
    private static final String NEW_USER_EMAIL = "new_user@mail.ru";
    private static final String PASSWORD = "password";
    private static final long BIRTH_DATE = 978307200000L; // 2001-01-01
    private static final String PHONE = "+7(900)0000000";
    private static final String BIRTH_DATE_STR = "2001-01-01";

    @Autowired
    private TestService testService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getItemTreeTest() throws Exception {
        this.mockMvc.perform(
            get(URL_PREFIX + "/tree"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].data").value(5))
                .andExpect(jsonPath("$.data[0].parent_id").value(0));
    }

    @Test
    void getItemTreeUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/tree"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getPersonTest() throws Exception {
        this.mockMvc.perform(
                    get(URL_PREFIX)
                        .param("id", String.valueOf(PERSON_ID)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(PERSON_ID))
                .andExpect(jsonPath("$.data.email").value(PERSON_EMAIL));
    }

    @Test
    void getPersonUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX)
                                .param("id", String.valueOf(PERSON_ID)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/users/deleteNewUser.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void setPersonTest() throws Exception {
        PersonDTO personDTO = PersonDTO.builder()
                .id(USER_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(NEW_USER_EMAIL)
                .password(PASSWORD)
                .birthDate(BIRTH_DATE)
                .phone(PHONE)
                .role(Role.CUSTOMER.toString())
                .build();
        this.mockMvc.perform(
                        post(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(personDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(Constants.OK));

        User user = userService.getByEmail(NEW_USER_EMAIL);
        assertEquals(LAST_NAME, user.getLastName());
        assertEquals(Role.CUSTOMER, user.getRole());
    }

    @Test
    void setPersonUnauthorizedTest() throws Exception {
        PersonDTO personDTO = PersonDTO.builder().build();
        this.mockMvc.perform(
                        post(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(personDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/users/addNewUser.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/users/deleteNewUser.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void updatePersonTest() throws Exception {
        PersonDTO personDTO = PersonDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(NEW_USER_EMAIL)
                .birthDate(BIRTH_DATE)
                .phone(PHONE)
                .role(Role.ADMIN.toString())
                .build();
        this.mockMvc.perform(
                        put(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(personDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(Constants.OK));

        User user = userService.getByEmail(NEW_USER_EMAIL);
        assertEquals(LAST_NAME, user.getLastName());
        assertEquals(BIRTH_DATE_STR, user.getBirthDate().toString());
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void updatePersonUnauthorizedTest() throws Exception {
        PersonDTO personDTO = PersonDTO.builder().build();
        this.mockMvc.perform(
                        put(URL_PREFIX)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(personDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
