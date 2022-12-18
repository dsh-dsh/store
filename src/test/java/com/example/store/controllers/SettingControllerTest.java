package com.example.store.controllers;

import com.example.store.components.SystemSettingsCash;
import com.example.store.model.dto.SettingDTO;
import com.example.store.model.dto.SettingDTOList;
import com.example.store.model.dto.UserDTO;
import com.example.store.model.dto.requests.IdsDTO;
import com.example.store.model.entities.PropertySetting;
import com.example.store.model.entities.User;
import com.example.store.model.entities.documents.Document;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.enums.SettingType;
import com.example.store.repositories.DocumentRepository;
import com.example.store.repositories.SettingRepository;
import com.example.store.services.SettingService;
import com.example.store.services.UserService;
import com.example.store.utils.Constants;
import com.example.store.utils.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
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
    @Autowired
    private SettingRepository settingRepository;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private User systemUser;
    @Autowired
    private SystemSettingsCash systemSettingsCash;
    @Autowired
    @Qualifier("blockingUserIds")
    protected List<Integer> blockingUserIds;

    @Test
    void getSettingsUnauthorizedTest()  throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX)
                                .param("userId", "1"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/settings/addSettings.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
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

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getHoldingSettingsTest()  throws Exception {
        systemSettingsCash.setSetting(SettingType.ADD_REST_FOR_HOLD_1C_DOCS,1);
        this.mockMvc.perform(
                        get(URL_PREFIX + "/system")
                                .param("type", "ADD_REST_FOR_HOLD_1C_DOCS"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("ADD_REST_FOR_HOLD_1C_DOCS"))
                .andExpect(jsonPath("$.data.property").value(1));
    }

    @Test
    void getHoldingSettingsUnauthorizedTest()  throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/system")
                                .param("type", "ADD_REST_FOR_HOLD_1C_DOCS"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getAveragePriceForPeriodCloseSettingsTest()  throws Exception {
        systemSettingsCash.setSetting(SettingType.PERIOD_AVERAGE_PRICE,1);
        this.mockMvc.perform(
                        get(URL_PREFIX + "/system")
                                .param("type", "PERIOD_AVERAGE_PRICE"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("PERIOD_AVERAGE_PRICE"))
                .andExpect(jsonPath("$.data.property").value(1));
    }

    @Test
    void getAveragePriceForPeriodCloseSettingsUnauthorizedTest()  throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/system")
                                .param("type", "PERIOD_AVERAGE_PRICE"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getAveragePriceForDocsSettingsTest()  throws Exception {
        systemSettingsCash.setSetting(SettingType.DOCS_AVERAGE_PRICE,1);
        this.mockMvc.perform(
                        get(URL_PREFIX + "/system")
                                .param("type", "DOCS_AVERAGE_PRICE"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("DOCS_AVERAGE_PRICE"))
                .andExpect(jsonPath("$.data.property").value(1));
    }

    @Test
    void getAveragePriceForDocsSettingsUnauthorizedTest()  throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/system")
                                .param("type", "DOCS_AVERAGE_PRICE"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getOurCompanySettingsTest()  throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/system")
                                .param("type", "OUR_COMPANY_ID"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("OUR_COMPANY_ID"))
                .andExpect(jsonPath("$.data.property").value(1));
    }

    @Test
    void getOurCompanySettingsUnauthorizedTest()  throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/system")
                                .param("type", "DOCS_AVERAGE_PRICE"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getIngredientDirSettingsTest()  throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/system")
                                .param("type", "INGREDIENT_DIR_ID"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("INGREDIENT_DIR_ID"))
                .andExpect(jsonPath("$.data.property").value(1));
    }

    @Test
    void getIngredientDirSettingsUnauthorizedTest()  throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/system")
                                .param("type", "INGREDIENT_DIR_ID"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getHoldingDialogEnableSettingTest()  throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/system")
                        .param("type", "HOLDING_DIALOG_ENABLE"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("HOLDING_DIALOG_ENABLE"))
                .andExpect(jsonPath("$.data.property").value(1));
    }

    @Test
    void getHoldingDialogEnableSettingUnauthorizedTest()  throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/system")
                                .param("type", "HOLDING_DIALOG_ENABLE"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getCheckHoldingEnableSettingTest()  throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/system")
                                .param("type", "CHECK_HOLDING_ENABLE"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.type").value("CHECK_HOLDING_ENABLE"))
                .andExpect(jsonPath("$.data.property").value(1));
    }

    @Test
    void getCheckHoldingEnableSettingUnauthorizedTest()  throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/system")
                                .param("type", "CHECK_HOLDING_ENABLE"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/period/add7DocList.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getBlockTimeIfHoldenChecksExistsTest()  throws Exception {
        List<Integer> currentList = List.copyOf(blockingUserIds);
        settingService.updateIdsSettingBean(SettingType.BLOCKING_USER_ID, List.of(6));
        this.mockMvc.perform(
                        get(URL_PREFIX + "/block/time"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data")
                        .value(Util.getLongLocalDateTime("16.10.22 01:00:00") + 402L));
        settingService.updateIdsSettingBean(SettingType.BLOCKING_USER_ID, currentList);
    }

    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getBlockTimeIfHoldenChecksNotExistsTest()  throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/block/time"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(Util.getLongLocalDate(LocalDate.parse(Constants.DEFAULT_PERIOD_START))));
    }

    @Test
    void getBlockTimeUnauthorizedTest()  throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/block/time"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

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

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setSettingsUnauthorizedTest()  throws Exception {
        this.mockMvc.perform(
                        post(URL_PREFIX + "/property"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/settings/addSettings.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void setDocTypeFilterPropertiesTest()  throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1);
        SettingDTOList settingDTOList = new SettingDTOList();
        settingDTOList.setUser(userDTO);
        settingDTOList.setSettings(getSettingDTOList());
        this.mockMvc.perform(
                        post(URL_PREFIX + "/doc/type/properties")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(settingDTOList)))
                .andDo(print())
                .andExpect(status().isOk());
        List<PropertySetting> settings = settingRepository.findByUser(userService.getById(1));
        assertFalse(settings.isEmpty());
        assertEquals(7, settings.size());
        assertEquals(0, settings.get(3).getProperty());

    }

    @Sql(value = "/sql/settings/addSettings.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setDocTypeFilterPropertiesUnauthorizedTest()  throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1);
        SettingDTOList settingDTOList = new SettingDTOList();
        settingDTOList.setUser(userDTO);
        settingDTOList.setSettings(getSettingDTOList());
        this.mockMvc.perform(
                        post(URL_PREFIX + "/doc/type/properties")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(settingDTOList)))
                .andDo(print())
                .andExpect(status().isUnauthorized());

    }

    private List<SettingDTO> getSettingDTOList() {
        return List.of(
            getSettingDTO(null, SettingType.POSTING_DOC_TYPE_FILTER, 1),
            getSettingDTO(null, SettingType.INVENTORY_DOC_TYPE_FILTER, 1),
            getSettingDTO(null, SettingType.CREDIT_ORDER_DOC_TYPE_FILTER, 1)
        );
    }

    private SettingDTO getSettingDTO(UserDTO userDTO, SettingType type, int property) {
        SettingDTO settingDTO = new SettingDTO();
        settingDTO.setUser(userDTO);
        settingDTO.setType(type.toString());
        settingDTO.setProperty(property);
        return settingDTO;
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void setAddShortageSettingTest()  throws Exception {
        SettingDTO settingDTO = getSettingDTO(SettingType.ADD_REST_FOR_HOLD_1C_DOCS.toString(), 0);
        this.mockMvc.perform(
                        post(URL_PREFIX + "/system/property")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(settingDTO)))
                .andDo(print())
                .andExpect(status().isOk());
        assertEquals(0, settingService.getSettingByType(systemUser, SettingType.ADD_REST_FOR_HOLD_1C_DOCS).getProperty());
        assertEquals(0, settingService
                .getSettingByType(systemUser, SettingType.ADD_REST_FOR_HOLD_1C_DOCS).getProperty());
        systemSettingsCash.setSetting(SettingType.ADD_REST_FOR_HOLD_1C_DOCS,1);

    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setAddShortageSettingUnauthorizedTest()  throws Exception {
        SettingDTO settingDTO = getSettingDTO(SettingType.ADD_REST_FOR_HOLD_1C_DOCS.toString(), 0);
        this.mockMvc.perform(
                        post(URL_PREFIX + "/system/property")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(settingDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void setAveragePriceForPeriodCloseSettingTest()  throws Exception {
        SettingDTO settingDTO = getSettingDTO(SettingType.PERIOD_AVERAGE_PRICE.toString(), 0);
        this.mockMvc.perform(
                        post(URL_PREFIX + "/system/property")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(settingDTO)))
                .andDo(print())
                .andExpect(status().isOk());
        User user = userService.getSystemAuthor();
        assertEquals(0, settingService.getSettingByType(user, SettingType.PERIOD_AVERAGE_PRICE).getProperty());
        assertEquals(0, settingService
                .getSettingByType(systemUser, SettingType.PERIOD_AVERAGE_PRICE).getProperty());
        systemSettingsCash.setSetting(SettingType.PERIOD_AVERAGE_PRICE,1);
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void setAveragePriceForDocsSettingTest()  throws Exception {
        SettingDTO settingDTO = getSettingDTO(SettingType.DOCS_AVERAGE_PRICE.toString(), 0);
        this.mockMvc.perform(
                        post(URL_PREFIX + "/system/property")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(settingDTO)))
                .andDo(print())
                .andExpect(status().isOk());
        assertEquals(0, settingService.getSettingByType(systemUser, SettingType.DOCS_AVERAGE_PRICE).getProperty());
        assertEquals(0, settingService
                .getSettingByType(systemUser, SettingType.DOCS_AVERAGE_PRICE).getProperty());
        systemSettingsCash.setSetting(SettingType.DOCS_AVERAGE_PRICE,1);
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void setIngredientDirSettingTest()  throws Exception {
        SettingDTO settingDTO = getSettingDTO(SettingType.INGREDIENT_DIR_ID.toString(), 0);
        this.mockMvc.perform(
                        post(URL_PREFIX + "/system/property")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(settingDTO)))
                .andDo(print())
                .andExpect(status().isOk());
        assertEquals(0, settingService.getSettingByType(systemUser, SettingType.INGREDIENT_DIR_ID).getProperty());
        assertEquals(0, settingService
                .getSettingByType(systemUser, SettingType.INGREDIENT_DIR_ID).getProperty());
        systemSettingsCash.setSetting(SettingType.INGREDIENT_DIR_ID,1);
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void setOurCompanySettingTest()  throws Exception {
        SettingDTO settingDTO = getSettingDTO(SettingType.OUR_COMPANY_ID.toString(), 0);
        this.mockMvc.perform(
                        post(URL_PREFIX + "/system/property")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(settingDTO)))
                .andDo(print())
                .andExpect(status().isOk());
        assertEquals(0, settingService.getSettingByType(systemUser, SettingType.OUR_COMPANY_ID).getProperty());
        assertEquals(0, settingService
                .getSettingByType(systemUser, SettingType.OUR_COMPANY_ID).getProperty());
        systemSettingsCash.setSetting(SettingType.OUR_COMPANY_ID,1);
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void setHoldingDialogEnableSettingTest()  throws Exception {
        SettingDTO settingDTO = getSettingDTO(SettingType.HOLDING_DIALOG_ENABLE.toString(), 0);
        this.mockMvc.perform(
                        post(URL_PREFIX + "/system/property")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(settingDTO)))
                .andDo(print())
                .andExpect(status().isOk());
        assertEquals(0, settingService.getSettingByType(systemUser, SettingType.HOLDING_DIALOG_ENABLE).getProperty());
        assertEquals(0, settingService
                .getSettingByType(systemUser, SettingType.HOLDING_DIALOG_ENABLE).getProperty());
        systemSettingsCash.setSetting(SettingType.HOLDING_DIALOG_ENABLE,1);
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void setCheckHoldingEnableSettingTest()  throws Exception {
        SettingDTO settingDTO = getSettingDTO(SettingType.CHECK_HOLDING_ENABLE.toString(), 0);
        this.mockMvc.perform(
                        post(URL_PREFIX + "/system/property")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(settingDTO)))
                .andDo(print())
                .andExpect(status().isOk());
        assertEquals(0, settingService.getSettingByType(systemUser, SettingType.CHECK_HOLDING_ENABLE).getProperty());
        assertEquals(0, settingService
                .getSettingByType(systemUser, SettingType.CHECK_HOLDING_ENABLE).getProperty());
        systemSettingsCash.setSetting(SettingType.CHECK_HOLDING_ENABLE,1);
    }

    @Sql(value = "/sql/period/addPeriods.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getCurrentPeriodTest() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/period"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.start_date").value(1648760400000L))
                .andExpect(jsonPath("$.data.end_date").value(1651266000000L));
    }

    @Test
    void getCurrentPeriodUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/period"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getDisabledItemsUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/disabled/items"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/settings/addIdsSettings.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getDisabledItemsTest() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/disabled/items"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ids.[0]").value(7))
                .andExpect(jsonPath("$.data.ids.[1]").doesNotExist());
    }

    @Test
    void getBlockingUsersUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/blocking/users"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/settings/addIdsSettings.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void getBlockingUsersTest() throws Exception {
        this.mockMvc.perform(
                        get(URL_PREFIX + "/blocking/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ids.[0]").value(1))
                .andExpect(jsonPath("$.data.ids.[1]").doesNotExist());
    }

    @Sql(value = "/sql/period/addPeriods.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/period/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void closePeriodTest() throws Exception {
        this.mockMvc.perform(
                        post(URL_PREFIX + "/period"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.start_date").value(1651352400000L))
                .andExpect(jsonPath("$.data.end_date").value(1653944400000L));
    }

    @Test
    void closePeriodUnauthorizedTest() throws Exception {
        this.mockMvc.perform(
                        post(URL_PREFIX + "/period"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setDisabledItemsUnauthorizedTest()  throws Exception {
        IdsDTO idsDTO = new IdsDTO(List.of(7, 8));
        this.mockMvc.perform(
                        post(URL_PREFIX + "/disabled/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(idsDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void setDisabledItemsTest()  throws Exception {
        IdsDTO idsDTO = new IdsDTO(List.of(7, 8));
        this.mockMvc.perform(
                        post(URL_PREFIX + "/disabled/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(idsDTO)))
                .andDo(print())
                .andExpect(status().isOk());
        List<PropertySetting> list = settingRepository.getByUserAndSettingType(systemUser, SettingType.DISABLED_ITEM_ID);
        assertEquals(2, list.size());
        assertEquals(7, list.get(0).getProperty());
        assertEquals(8, list.get(1).getProperty());
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setBlockingUsersUnauthorizedTest()  throws Exception {
        IdsDTO idsDTO = new IdsDTO(List.of(1, 2));
        this.mockMvc.perform(
                        post(URL_PREFIX + "/blocking/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(idsDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void setBlockingUsersTest()  throws Exception {
        IdsDTO idsDTO = new IdsDTO(List.of(1, 2));
        this.mockMvc.perform(
                        post(URL_PREFIX + "/blocking/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(idsDTO)))
                .andDo(print())
                .andExpect(status().isOk());
        List<PropertySetting> list = settingRepository.getByUserAndSettingType(systemUser, SettingType.BLOCKING_USER_ID);
        assertEquals(2, list.size());
        assertEquals(1, list.get(0).getProperty());
        assertEquals(2, list.get(1).getProperty());
    }

    private SettingDTO getSettingDTO(String type, int property) {
        UserDTO userDTO = new UserDTO();
        SettingDTO settingDTO = new SettingDTO();
        settingDTO.setUser(userDTO);
        settingDTO.setType(type);
        settingDTO.setProperty(property);
        return settingDTO;
    }
}