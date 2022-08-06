package com.example.store.controllers;

import com.example.store.model.dto.requests.AuthUserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest")
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private static final String URL_PREFIX = "/api/v1/auth";

    @Test
    void loginWithRightCredentialsTest() throws Exception{

        AuthUserRequest request = new AuthUserRequest();
        request.setLogin("admin@mail.ru");
        request.setPassword("12345678");

        this.mockMvc.perform(
                        post(URL_PREFIX + "/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andExpect(jsonPath("$.data.token").isString());
    }

    @Test
    void loginWithWrongCredentialsTest() throws Exception{

        AuthUserRequest request = new AuthUserRequest();
        request.setLogin("customer@mail.ru");
        request.setPassword("wrongPassword");

        this.mockMvc.perform(
                        post(URL_PREFIX + "/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithNoSuchUserTest() throws Exception{

        AuthUserRequest request = new AuthUserRequest();
        request.setLogin("no_such@user.ru");
        request.setPassword("password");

        this.mockMvc.perform(
                        post(URL_PREFIX + "/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}