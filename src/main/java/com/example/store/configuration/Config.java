package com.example.store.configuration;

import com.example.store.model.entities.PropertySetting;
import com.example.store.model.entities.User;
import com.example.store.model.enums.SettingType;
import com.example.store.repositories.SettingRepository;
import com.example.store.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
public class Config {

    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

    @Bean("systemUser")
    public User getSystemUser(UserService userService) {
        return userService.getSystemAuthor();
    }

    @Bean
    @DependsOn("systemUser")
    @Qualifier("addRestForHold")
    public PropertySetting getAddRestForHoldSetting(SettingRepository settingRepository, User systemUser) {
        return settingRepository
                .findByUserAndSettingType(systemUser, SettingType.ADD_REST_FOR_HOLD_1C_DOCS)
                .orElse(PropertySetting.of(SettingType.ADD_REST_FOR_HOLD_1C_DOCS, systemUser,1));
    }

    @Bean
    @DependsOn("systemUser")
    @Qualifier("periodAveragePrice")
    public PropertySetting getPeriodAveragePriceSetting(SettingRepository settingRepository, User systemUser) {
        return settingRepository
                .findByUserAndSettingType(systemUser, SettingType.PERIOD_AVERAGE_PRICE)
                .orElse(PropertySetting.of(SettingType.PERIOD_AVERAGE_PRICE, systemUser,1));
    }

    @Bean
    @DependsOn("systemUser")
    @Qualifier("docsAveragePrice")
    public PropertySetting getDocsAveragePriceSetting(SettingRepository settingRepository, User systemUser) {
        return settingRepository
                .findByUserAndSettingType(systemUser, SettingType.DOCS_AVERAGE_PRICE)
                .orElse(PropertySetting.of(SettingType.DOCS_AVERAGE_PRICE, systemUser,1));
    }

    @Bean
    @DependsOn("systemUser")
    @Qualifier("ourCompany")
    public PropertySetting getOurCompanySetting(SettingRepository settingRepository, User systemUser) {
        return settingRepository
                .findByUserAndSettingType(systemUser, SettingType.OUR_COMPANY_ID)
                .orElse(PropertySetting.of(SettingType.OUR_COMPANY_ID, systemUser,1));
    }

    @Bean
    @DependsOn("systemUser")
    @Qualifier("ingredientDir")
    public PropertySetting getIngredientDirSetting(SettingRepository settingRepository, User systemUser) {
        return settingRepository
                .findByUserAndSettingType(systemUser, SettingType.INGREDIENT_DIR_ID)
                .orElse(PropertySetting.of(SettingType.INGREDIENT_DIR_ID, systemUser,2));
    }

    @Bean
    @DependsOn("systemUser")
    @Qualifier("holdingDialogEnable")
    public PropertySetting getHoldingDialogEnableSetting(SettingRepository settingRepository, User systemUser) {
        return settingRepository
                .findByUserAndSettingType(systemUser, SettingType.HOLDING_DIALOG_ENABLE)
                .orElse(PropertySetting.of(SettingType.HOLDING_DIALOG_ENABLE, systemUser,1));
    }

    @Bean
    @DependsOn("systemUser")
    @Qualifier("checkHoldingEnable")
    public PropertySetting getCheckHoldingEnableSetting(SettingRepository settingRepository, User systemUser) {
        return settingRepository
                .findByUserAndSettingType(systemUser, SettingType.CHECK_HOLDING_ENABLE)
                .orElse(PropertySetting.of(SettingType.CHECK_HOLDING_ENABLE, systemUser,1));
    }

    @Bean
    @DependsOn("systemUser")
    @Qualifier("enableDocsBlockSetting")
    public PropertySetting getEnableDocsBlockSetting(SettingRepository settingRepository, User systemUser) {
        return settingRepository
                .findByUserAndSettingType(systemUser, SettingType.DOC_BLOCK_ENABLE)
                .orElse(PropertySetting.of(SettingType.DOC_BLOCK_ENABLE, systemUser,1));
    }

    @Bean
    @Qualifier("disabledItemIds")
    public List<Integer> getDisabledItemIds(SettingRepository settingRepository, User systemUser) {
        return settingRepository.getByUserAndSettingType(systemUser, SettingType.DISABLED_ITEM_ID)
                .stream().map(PropertySetting::getProperty).collect(Collectors.toList());
    }

}
