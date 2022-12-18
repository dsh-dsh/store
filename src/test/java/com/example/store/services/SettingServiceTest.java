package com.example.store.services;

import com.example.store.components.SystemSettingsCash;
import com.example.store.model.dto.SettingDTO;
import com.example.store.model.dto.SettingDTOList;
import com.example.store.model.dto.UserDTO;
import com.example.store.model.dto.requests.IdsDTO;
import com.example.store.model.entities.PropertySetting;
import com.example.store.model.entities.User;
import com.example.store.model.enums.SettingType;
import com.example.store.repositories.SettingRepository;
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
    private SettingRepository settingRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private User systemUser;
    @Autowired
    private SystemSettingsCash systemSettingsCash;
    @Autowired
    @Qualifier("disabledItemIds")
    protected List<Integer> disabledItemIds;
    @Autowired
    @Qualifier("blockingUserIds")
    protected List<Integer> blockingUserIds;

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
        SettingDTO dto = settingService.mapToDTO(setting, userDTO);
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

    @Sql(value = "/sql/settings/addSettings.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setPropertyUserTest() {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1);
        SettingDTO dto = new SettingDTO(userDTO, SettingType.REQUEST_DOC_TYPE_FILTER.toString(), 5);
        User user = userService.getById(1);
        settingService.setProperty(user, dto);
        PropertySetting setting = settingService.getSettingByType(user, SettingType.REQUEST_DOC_TYPE_FILTER);
        assertEquals(5, setting.getProperty());
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setAddShortageSettingTest() {
        SettingDTO dto = new SettingDTO(null, SettingType.ADD_REST_FOR_HOLD_1C_DOCS.toString(), 0);
        settingService.setSystemProperty(dto);
        PropertySetting setting = settingService.getSystemSetting(SettingType.ADD_REST_FOR_HOLD_1C_DOCS);
        assertEquals(0, setting.getProperty());
        assertEquals(0, settingService
                .getSettingByType(systemUser, SettingType.ADD_REST_FOR_HOLD_1C_DOCS).getProperty());
        systemSettingsCash.setSetting(SettingType.ADD_REST_FOR_HOLD_1C_DOCS,1);
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setAveragePriceForPeriodCloseSettingTest() {
        SettingDTO dto = new SettingDTO(null, SettingType.PERIOD_AVERAGE_PRICE.toString(), 0);
        settingService.setSystemProperty(dto);
        PropertySetting setting = settingService.getSystemSetting(SettingType.PERIOD_AVERAGE_PRICE);
        assertEquals(0, setting.getProperty());
        assertEquals(0, settingService
                .getSettingByType(systemUser, SettingType.PERIOD_AVERAGE_PRICE).getProperty());
        systemSettingsCash.setSetting(SettingType.PERIOD_AVERAGE_PRICE,1);
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setAveragePriceForDocsSettingTest() {
        SettingDTO dto = new SettingDTO(null, SettingType.DOCS_AVERAGE_PRICE.toString(), 0);
        settingService.setSystemProperty(dto);
        PropertySetting setting = settingService.getSystemSetting(SettingType.DOCS_AVERAGE_PRICE);
        assertEquals(0, setting.getProperty());
        assertEquals(0, settingService
                .getSettingByType(systemUser, SettingType.DOCS_AVERAGE_PRICE).getProperty());
        systemSettingsCash.setSetting(SettingType.DOCS_AVERAGE_PRICE,1);
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setOurCompanySettingTest() {
        SettingDTO dto = new SettingDTO(null, SettingType.OUR_COMPANY_ID.toString(), 0);
        settingService.setSystemProperty(dto);
        PropertySetting setting = settingService.getSystemSetting(SettingType.OUR_COMPANY_ID);
        assertEquals(0, setting.getProperty());
        assertEquals(0, settingService
                .getSettingByType(systemUser, SettingType.OUR_COMPANY_ID).getProperty());
        systemSettingsCash.setSetting(SettingType.OUR_COMPANY_ID,1);
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setIngredientDirSettingTest() {
        SettingDTO dto = new SettingDTO(null, SettingType.INGREDIENT_DIR_ID.toString(), 10);
        settingService.setSystemProperty(dto);
        PropertySetting setting = settingService.getSystemSetting(SettingType.INGREDIENT_DIR_ID);
        assertEquals(10, setting.getProperty());
        assertEquals(10, settingService
                .getSettingByType(systemUser, SettingType.INGREDIENT_DIR_ID).getProperty());
        systemSettingsCash.setSetting(SettingType.INGREDIENT_DIR_ID,1);
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setHoldingDialogEnableSettingTest() {
        SettingDTO dto = new SettingDTO(null, SettingType.HOLDING_DIALOG_ENABLE.toString(), 0);
        settingService.setSystemProperty(dto);
        PropertySetting setting = settingService.getSystemSetting(SettingType.HOLDING_DIALOG_ENABLE);
        assertEquals(0, setting.getProperty());
        assertEquals(0, settingService
                .getSettingByType(systemUser, SettingType.HOLDING_DIALOG_ENABLE).getProperty());
        systemSettingsCash.setSetting(SettingType.HOLDING_DIALOG_ENABLE,1);
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setEnableDocsBlockSettingTest() {
        SettingDTO dto = new SettingDTO(null, SettingType.DOC_BLOCK_ENABLE.toString(), 0);
        settingService.setSystemProperty(dto);
        PropertySetting setting = settingService.getSystemSetting(SettingType.DOC_BLOCK_ENABLE);
        assertEquals(0, setting.getProperty());
        assertEquals(0, settingService
                .getSettingByType(systemUser, SettingType.DOC_BLOCK_ENABLE).getProperty());
        systemSettingsCash.setSetting(SettingType.DOC_BLOCK_ENABLE,1);
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setCheckHoldingEnableSettingTest() {
        SettingDTO dto = new SettingDTO(null, SettingType.CHECK_HOLDING_ENABLE.toString(), 0);
        settingService.setSystemProperty(dto);
        PropertySetting setting = settingService.getSystemSetting(SettingType.CHECK_HOLDING_ENABLE);
        assertEquals(0, setting.getProperty());
        assertEquals(0, settingService
                .getSettingByType(systemUser, SettingType.CHECK_HOLDING_ENABLE).getProperty());
        systemSettingsCash.setSetting(SettingType.CHECK_HOLDING_ENABLE,1);
    }

    @Sql(value = "/sql/settings/addDocFiltersSettings.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setDocTypeFilterPropertiesTest() {
        SettingDTOList dtoList = new SettingDTOList();
        dtoList.setUser(new UserDTO(4, "", ""));
        dtoList.setSettings(
                List.of(
                    getSettingDTO("REQUEST_DOC_TYPE_FILTER", 1),
                    getSettingDTO("INVENTORY_DOC_TYPE_FILTER",0))
        );
        settingService.setDocTypeFilterProperties(dtoList);
        List<PropertySetting> settings = settingRepository.findByUser(userService.getById(4));
        assertEquals(2, settings.size());
        assertEquals(SettingType.REQUEST_DOC_TYPE_FILTER, settings.get(0).getSettingType());
        assertEquals(1, settings.get(0).getProperty());
        assertEquals(SettingType.INVENTORY_DOC_TYPE_FILTER, settings.get(1).getSettingType());
        assertEquals(0, settings.get(1).getProperty());
    }

    private SettingDTO getSettingDTO(String type, int property) {
        SettingDTO dto = new SettingDTO();
        dto.setType(type);
        dto.setProperty(property);
        return dto;
    }

    @Test
    void getAddShortageForHoldSettingTest() {
        assertEquals(1, settingService
                .getSystemSettingDTO(SettingType.ADD_REST_FOR_HOLD_1C_DOCS.toString()).getProperty());
    }

    @Test
    void getAveragePriceForPeriodCloseSettingTest() {
        assertEquals(1, settingService
                .getSystemSettingDTO(SettingType.PERIOD_AVERAGE_PRICE.toString()).getProperty());
    }

    @Test
    void getAveragePriceForDocsSettingTest() {
        assertEquals(1, settingService
                .getSystemSettingDTO(SettingType.DOCS_AVERAGE_PRICE.toString()).getProperty());
    }

    @Test
    void getOurCompanyIdSettingTest() {
        assertEquals(1, settingService
                .getSystemSettingDTO(SettingType.OUR_COMPANY_ID.toString()).getProperty());
    }

    @Test
    void getIngredientDirIdSettingTest() {
        systemSettingsCash.setSetting(SettingType.INGREDIENT_DIR_ID, 1);
        assertEquals(1, settingService
                .getSystemSetting(SettingType.INGREDIENT_DIR_ID).getProperty());
    }

    @Test
    void getHoldingDialogEnableSettingTest() {
        assertEquals(1, settingService
                .getSystemSetting(SettingType.HOLDING_DIALOG_ENABLE).getProperty());
    }

    @Test
    void getCheckHoldingEnableSettingTest() {
        assertEquals(1, settingService
                .getSystemSetting(SettingType.CHECK_HOLDING_ENABLE).getProperty());
    }

    @Test
    void getEnableDocsBlockSettingTest() {
        assertEquals(1, settingService
                .getSystemSetting(SettingType.DOC_BLOCK_ENABLE).getProperty());
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setDisabledItemsSettingListTest() {
        IdsDTO idsDTO = new IdsDTO(List.of(7, 8));
        settingService.setIdSettingList(idsDTO, SettingType.DISABLED_ITEM_ID);
        List<PropertySetting> list = settingRepository.getByUserAndSettingType(systemUser, SettingType.DISABLED_ITEM_ID);
        assertEquals(2, list.size());
        assertEquals(7, list.get(0).getProperty());
        assertEquals(8, list.get(1).getProperty());
    }

    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setBlockingUsersSettingListTest() {
        IdsDTO idsDTO = new IdsDTO(List.of(1, 2));
        settingService.setIdSettingList(idsDTO, SettingType.BLOCKING_USER_ID);
        List<PropertySetting> list = settingRepository.getByUserAndSettingType(systemUser, SettingType.BLOCKING_USER_ID);
        assertEquals(2, list.size());
        assertEquals(1, list.get(0).getProperty());
        assertEquals(2, list.get(1).getProperty());

    }

    @Test
    void updateDisabledItemsSettingBeanTest() {
        List<Integer> currentList = List.copyOf(disabledItemIds);
        settingService.updateIdsSettingBean(SettingType.DISABLED_ITEM_ID, List.of(10,11));
        assertEquals(10, disabledItemIds.get(0));
        assertEquals(11, disabledItemIds.get(1));
        settingService.updateIdsSettingBean(SettingType.DISABLED_ITEM_ID, currentList);
    }

    @Test
    void updateBlockingUsersSettingBeanTest() {
        List<Integer> currentList = List.copyOf(blockingUserIds);
        settingService.updateIdsSettingBean(SettingType.BLOCKING_USER_ID, List.of(10,11));
        assertEquals(10, blockingUserIds.get(0));
        assertEquals(11, blockingUserIds.get(1));
        settingService.updateIdsSettingBean(SettingType.BLOCKING_USER_ID, currentList);
    }

    @Sql(value = "/sql/settings/addIdsSettings.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getDisabledItemsSettingListTest() {
        assertEquals(7, settingService.getIdSettingList(SettingType.DISABLED_ITEM_ID).getIds().get(0));
    }

    @Sql(value = "/sql/settings/addIdsSettings.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/settings/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getBlockingUsersSettingListTest() {
        assertEquals(1, settingService.getIdSettingList(SettingType.BLOCKING_USER_ID).getIds().get(0));
    }
}