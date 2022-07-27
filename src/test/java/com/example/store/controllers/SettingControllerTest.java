package com.example.store.controllers;

import com.example.store.model.dto.SettingDTO;
import com.example.store.model.dto.UserDTO;
import com.example.store.model.entities.User;
import com.example.store.model.enums.SettingType;
import com.example.store.services.SettingService;
import com.example.store.services.UserService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest?serverTimezone=UTC")
@SpringBootTest
@AutoConfigureMockMvc
class SettingControllerTest {

    private static final String URL_PREFIX = "/api/v1/setting";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SettingService settingService;
    @Autowired
    private UserService userService;

    @Test
    void getSettingsUnauthorizedTest()  throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX)
                                .param("userId", "1"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = {"/sql/hold1CDocs/addSystemUser.sql",
            "/sql/settings/addSettings.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getSettingsTest()  throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX)
                                .param("userId", "1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.[0].user.id").value(1))
                .andExpect(jsonPath("$.data.[0].type").value("PROJECT"))
                .andExpect(jsonPath("$.data.[0].property").value(1))
                .andExpect(jsonPath("$.data.[1].user.id").value(1))
                .andExpect(jsonPath("$.data.[1].type").value("STORAGE_TO"))
                .andExpect(jsonPath("$.data.[1].property").value(2))
                .andExpect(jsonPath("$.data.[2].user.id").value(1))
                .andExpect(jsonPath("$.data.[2].type").value("STORAGE_FROM"))
                .andExpect(jsonPath("$.data.[2].property").value(3));
    }

    @Sql(value = {"/sql/hold1CDocs/addSystemUser.sql",
            "/sql/settings/addHoldingSetting.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getHoldingSettingsTest()  throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/add/shortage"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.id").value(5))
                .andExpect(jsonPath("$.data.type").value("ADD_REST_FOR_HOLD"))
                .andExpect(jsonPath("$.data.property").value(1));
    }

    @Sql(value = {"/sql/hold1CDocs/addSystemUser.sql",
            "/sql/settings/addHoldingSetting.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getHoldingSettingsUnauthorizedTest()  throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/add/shortage"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = {"/sql/hold1CDocs/addSystemUser.sql",
            "/sql/settings/addSettings.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void setSettingsTest()  throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1);
        SettingDTO settingDTO = new SettingDTO();
        settingDTO.setUser(userDTO);
        settingDTO.setType(SettingType.PROJECT.toString());
        settingDTO.setProperty(3);
        this.mockMvc.perform(
                        post(URL_PREFIX + "/property")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(settingDTO)))
                .andDo(print())
                .andExpect(status().isOk());
        User user = userService.getById(1);
        assertEquals(3, settingService.getSettingByType(user, SettingType.PROJECT).getProperty());
    }

    @Sql(value = {"/sql/hold1CDocs/addSystemUser.sql",
            "/sql/settings/addSettings.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setSettingsUnauthorizedTest()  throws Exception {
        this.mockMvc.perform(
                        post(URL_PREFIX + "/property"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = {"/sql/hold1CDocs/addSystemUser.sql",
            "/sql/settings/addHoldingSetting.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void setAddShortageSettingTest()  throws Exception {
        UserDTO userDTO = new UserDTO();
        SettingDTO settingDTO = new SettingDTO();
        settingDTO.setUser(userDTO);
        settingDTO.setType(SettingType.ADD_REST_FOR_HOLD.toString());
        settingDTO.setProperty(0);
        this.mockMvc.perform(
                        post(URL_PREFIX + "/add/shortage")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(settingDTO)))
                .andDo(print())
                .andExpect(status().isOk());
        User user = userService.getSystemAuthor();
        assertEquals(0, settingService.getSettingByType(user, SettingType.ADD_REST_FOR_HOLD).getProperty());
    }

    @Sql(value = {"/sql/hold1CDocs/addSystemUser.sql",
            "/sql/settings/addHoldingSetting.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setAddShortageSettingUnauthorizedTest()  throws Exception {
        UserDTO userDTO = new UserDTO();
        SettingDTO settingDTO = new SettingDTO();
        settingDTO.setUser(userDTO);
        settingDTO.setType(SettingType.ADD_REST_FOR_HOLD.toString());
        settingDTO.setProperty(0);
        this.mockMvc.perform(
                        post(URL_PREFIX + "/add/shortage")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(settingDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}