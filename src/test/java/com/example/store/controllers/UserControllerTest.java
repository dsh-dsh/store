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
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String URL_PREFIX = "/api/v1/users";
    private static final int PERSON_ID = 1;
    private static final String PERSON_EMAIL = "customer@mail.ru";

    private static final int USER_ID = 1;
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Smiths";
    private static final String NEW_USER_EMAIL = "new_user@mail.ru";
    private static final String PASSWORD = "password";
    private static final String BIRTH_DATE = "2001-01-01";
    private static final String REG_DATE = "2022-01-01T10:30:00";
    private static final String PHONE = "+7(900)0000000";

    @Autowired
    private TestService testService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;

    @Test
    void getPerson() throws Exception {

        this.mockMvc.perform(
                    get(URL_PREFIX)
                        .param("id", String.valueOf(PERSON_ID)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(PERSON_ID))
                .andExpect(jsonPath("$.data.email").value(PERSON_EMAIL));

    }

//    @Sql(value = "/sql/users/addPostingDoc.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/users/deleteNewUser.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setPerson() throws Exception {

        PersonDTO personDTO = PersonDTO.builder()
                .id(USER_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(NEW_USER_EMAIL)
                .password(PASSWORD)
                .birthDate(testService.dateToLong(BIRTH_DATE))
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
        assertEquals(user.getLastName(), LAST_NAME);
        assertEquals(user.getRole(), Role.CUSTOMER);

    }

    @Sql(value = "/sql/users/addNewUser.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/users/deleteNewUser.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updatePerson() throws Exception {

        PersonDTO personDTO = PersonDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(NEW_USER_EMAIL)
                .birthDate(testService.dateToLong(BIRTH_DATE))
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
        assertEquals(user.getLastName(), LAST_NAME);
        assertEquals(user.getBirthDate().toString(), BIRTH_DATE);
        assertEquals(user.getRole(), Role.ADMIN);

    }

}
