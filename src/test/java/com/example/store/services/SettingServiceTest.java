package com.example.store.services;

import com.example.store.model.dto.SettingDTO;
import com.example.store.model.dto.UserDTO;
import com.example.store.model.entities.DefaultPropertySetting;
import com.example.store.model.entities.User;
import com.example.store.model.enums.SettingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties =
        "spring.datasource.url=jdbc:mysql://localhost:3306/skladtest")
@SpringBootTest
@AutoConfigureMockMvc
class SettingServiceTest {

    @Autowired
    private SettingService settingService;
    @Autowired
    private UserService userService;

    @Sql(value = "/sql/settings/addSettings.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getSettingByTypeTest() {
        User user = userService.getById(1);
        DefaultPropertySetting settings;
        settings = settingService.getSettingByType(user, SettingType.PROJECT);
        assertEquals(1, settings.getProperty());
        settings = settingService.getSettingByType(user, SettingType.STORAGE_TO);
        assertEquals(2, settings.getProperty());
        settings = settingService.getSettingByType(user, SettingType.STORAGE_FROM);
        assertEquals(3, settings.getProperty());
    }

    @Sql(value = "/sql/settings/addSettings.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getSettingsByUserTest() {
        List<SettingDTO> settings = settingService.getSettingsByUser(1).getData();
        assertEquals(1, settings.get(0).getUser().getId());
        assertEquals(1, settings.get(0).getProperty());
    }

    @Sql(value = "/sql/settings/addSettings.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getSettingDTOTest() {
        User user = userService.getById(1);
        UserDTO userDTO = new UserDTO();
        DefaultPropertySetting setting = settingService.getSettingByType(user, SettingType.STORAGE_FROM);
        SettingDTO dto = settingService.getSettingDTO(setting, userDTO);
        assertEquals(userDTO, dto.getUser());
        assertEquals(3, dto.getProperty());
    }

    @Sql(value = "/sql/settings/addSettings.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setPropertyTest() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1);
        SettingDTO dto = new SettingDTO(userDTO, SettingType.STORAGE_TO.toString(), 5);
        settingService.setProperty(dto);
        User user = userService.getById(1);
        DefaultPropertySetting settings = settingService.getSettingByType(user, SettingType.STORAGE_TO);
        assertEquals(5, settings.getProperty());
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setAddShortageSettingTest() {
        SettingDTO dto = new SettingDTO();
        dto.setProperty(1);
        settingService.setAddShortageSetting(dto);
        User user = userService.getById(6);
        DefaultPropertySetting setting = settingService.getSettingByType(user, SettingType.ADD_REST_FOR_HOLD);
        assertEquals(1, setting.getProperty());
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setAveragePriceForPeriodCloseSettingTest() {
        SettingDTO dto = new SettingDTO();
        dto.setProperty(1);
        settingService.setAveragePriceForPeriodCloseSetting(dto);
        User user = userService.getById(6);
        DefaultPropertySetting setting = settingService.getSettingByType(user, SettingType.PERIOD_AVERAGE_PRICE);
        assertEquals(1, setting.getProperty());
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setAveragePriceForDocsSettingTest() {
        SettingDTO dto = new SettingDTO();
        dto.setProperty(1);
        settingService.setAveragePriceForDocsSetting(dto);
        User user = userService.getById(6);
        DefaultPropertySetting setting = settingService.getSettingByType(user, SettingType.DOCS_AVERAGE_PRICE);
        assertEquals(1, setting.getProperty());
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getAddShortageForHoldSettingTest() {
        assertEquals(1, settingService.getAddShortageForHoldSetting().getData().getProperty());
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getAveragePriceForPeriodCloseSettingsTest() {
        assertEquals(1, settingService.getAveragePriceForPeriodCloseSettings().getData().getProperty());
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getAveragePriceForDocsSettingsTest() {
        assertEquals(1, settingService.getAveragePriceForDocsSettings().getData().getProperty());
    }
}