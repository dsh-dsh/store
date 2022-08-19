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

@Configuration
public class Config {

    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

    @Bean
    public User getSystemUser(UserService userService) {
        return userService.getSystemAuthor();
    }

    @Bean
    @Qualifier("addRestForHold")
    public PropertySetting getAddRestForHoldSetting(SettingRepository settingRepository, User systemUser) {
        return settingRepository
                .findByUserAndSettingType(systemUser, SettingType.ADD_REST_FOR_HOLD_1C_DOCS)
                .orElse(null);
    }

    @Bean
    @Qualifier("periodAveragePrice")
    public PropertySetting getPeriodAveragePriceSetting(SettingRepository settingRepository, User systemUser) {
        return settingRepository
                .findByUserAndSettingType(systemUser, SettingType.PERIOD_AVERAGE_PRICE)
                .orElse(null);
    }

    @Bean
    @Qualifier("docsAveragePrice")
    public PropertySetting getDocsAveragePriceSetting(SettingRepository settingRepository, User systemUser) {
        return settingRepository
                .findByUserAndSettingType(systemUser, SettingType.DOCS_AVERAGE_PRICE)
                .orElse(null);
    }

}
