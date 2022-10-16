package com.example.store.services;

import com.example.store.model.dto.SettingDTO;
import com.example.store.model.dto.UserDTO;
import com.example.store.model.entities.PropertySetting;
import com.example.store.model.entities.User;
import com.example.store.model.enums.SettingType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class SettingServiceTest {

    @Autowired
    private SettingService settingService;
    @Autowired
    private UserService userService;
    @Autowired
    private User systemUser;
    @Autowired
    @Qualifier("addRestForHold")
    protected PropertySetting addRestForHoldSetting;
    @Autowired
    @Qualifier("periodAveragePrice")
    private PropertySetting periodAveragePriceSetting;
    @Autowired
    @Qualifier("docsAveragePrice")
    private PropertySetting docsAveragePriceSetting;
    @Autowired
    @Qualifier("ourCompany")
    private PropertySetting ourCompanySetting;
    @Autowired
    @Qualifier("ingredientDir")
    private PropertySetting ingredientDirSetting;
    @Autowired
    @Qualifier("holdingDialogEnable")
    private PropertySetting holdingDialogEnableSetting;
    @Autowired
    @Qualifier("checkHoldingEnable")
    private PropertySetting checkHoldingEnableSetting;

    @Sql(value = "/sql/settings/addSettings.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getSettingByTypeTest() {
        User user = userService.getById(1);
        PropertySetting settings;
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
        List<SettingDTO> settings = settingService.getSettingsByUser(1);
        assertEquals(1, settings.get(0).getUser().getId());
        assertEquals(1, settings.get(0).getProperty());
    }

    @Sql(value = "/sql/settings/addSettings.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getSettingDTOTest() {
        User user = userService.getById(1);
        UserDTO userDTO = new UserDTO();
        PropertySetting setting = settingService.getSettingByType(user, SettingType.STORAGE_FROM);
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
        PropertySetting settings = settingService.getSettingByType(user, SettingType.STORAGE_TO);
        assertEquals(5, settings.getProperty());
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setAddShortageSettingTest() {
        SettingDTO dto = new SettingDTO();
        dto.setProperty(0);
        settingService.setAddShortageSetting(dto);
        PropertySetting setting = settingService.getSettingByType(systemUser, SettingType.ADD_REST_FOR_HOLD_1C_DOCS);
        assertEquals(0, setting.getProperty());
        assertEquals(0, addRestForHoldSetting.getProperty());
        addRestForHoldSetting.setProperty(1);
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setAveragePriceForPeriodCloseSettingTest() {
        SettingDTO dto = new SettingDTO();
        dto.setProperty(0);
        settingService.setAveragePriceForPeriodCloseSetting(dto);
        PropertySetting setting = settingService.getSettingByType(systemUser, SettingType.PERIOD_AVERAGE_PRICE);
        assertEquals(0, setting.getProperty());
        assertEquals(0, periodAveragePriceSetting.getProperty());
        periodAveragePriceSetting.setProperty(1);
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setAveragePriceForDocsSettingTest() {
        SettingDTO dto = new SettingDTO();
        dto.setProperty(0);
        settingService.setAveragePriceForDocsSetting(dto);
        PropertySetting setting = settingService.getSettingByType(systemUser, SettingType.DOCS_AVERAGE_PRICE);
        assertEquals(0, setting.getProperty());
        assertEquals(0, docsAveragePriceSetting.getProperty());
        docsAveragePriceSetting.setProperty(1);
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setOurCompanySettingTest() {
        SettingDTO dto = new SettingDTO();
        dto.setProperty(0);
        settingService.setOurCompanySetting(dto);
        PropertySetting setting = settingService.getSettingByType(systemUser, SettingType.OUR_COMPANY_ID);
        assertEquals(0, setting.getProperty());
        assertEquals(0, ourCompanySetting.getProperty());
        ourCompanySetting.setProperty(1);
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setIngredientDirSettingTest() {
        SettingDTO dto = new SettingDTO();
        dto.setProperty(10);
        settingService.setIngredientDirSetting(dto);
        PropertySetting setting = settingService.getSettingByType(systemUser, SettingType.INGREDIENT_DIR_ID);
        assertEquals(10, setting.getProperty());
        assertEquals(10, ingredientDirSetting.getProperty());
        ingredientDirSetting.setProperty(2);
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setHoldingDialogEnableSettingTest() {
        SettingDTO dto = new SettingDTO();
        dto.setProperty(0);
        settingService.setHoldingDialogEnableSetting(dto);
        PropertySetting setting = settingService.getSettingByType(systemUser, SettingType.HOLDING_DIALOG_ENABLE);
        assertEquals(0, setting.getProperty());
        assertEquals(0, holdingDialogEnableSetting.getProperty());
        holdingDialogEnableSetting.setProperty(1);
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setCheckHoldingEnableSettingTest() {
        SettingDTO dto = new SettingDTO();
        dto.setProperty(0);
        settingService.setCheckHoldingEnableSetting(dto);
        PropertySetting setting = settingService.getSettingByType(systemUser, SettingType.CHECK_HOLDING_ENABLE);
        assertEquals(0, setting.getProperty());
        assertEquals(0, checkHoldingEnableSetting.getProperty());
        checkHoldingEnableSetting.setProperty(1);
    }

    @Test
    void getAddShortageForHoldSettingTest() {
        assertEquals(1, settingService.getAddShortageForHoldSetting().getProperty());
    }

    @Test
    void getAveragePriceForPeriodCloseSettingTest() {
        assertEquals(1, settingService.getAveragePriceForPeriodCloseSetting().getProperty());
    }

    @Test
    void getAveragePriceForDocsSettingTest() {
        assertEquals(1, settingService.getAveragePriceForDocsSetting().getProperty());
    }

    @Test
    void getOurCompanyIdSettingTest() {
        assertEquals(1, settingService.getOurCompanySetting().getProperty());
    }

    @Test
    void getIngredientDirIdSettingTest() {
        assertEquals(2, settingService.getIngredientDirSetting().getProperty());
    }

    @Test
    void getHoldingDialogEnableSettingTest() {
        assertEquals(1, settingService.getHoldingDialogEnableSetting().getProperty());
    }

    @Test
    void getCheckHoldingEnableSettingTest() {
        assertEquals(1, settingService.getCheckHoldingEnableSetting().getProperty());
    }
}