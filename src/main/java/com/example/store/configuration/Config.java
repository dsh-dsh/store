package com.example.store.configuration;

import com.example.store.components.doc_filters.*;
import com.example.store.model.entities.PropertySetting;
import com.example.store.model.entities.User;
import com.example.store.model.enums.SettingType;
import com.example.store.repositories.SettingRepository;
import com.example.store.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    @Qualifier("disabledItemIds")
    public List<Integer> getDisabledItemIds(SettingRepository settingRepository, User systemUser) {
        return settingRepository.getByUserAndSettingType(systemUser, SettingType.DISABLED_ITEM_ID)
                .stream().map(PropertySetting::getProperty).collect(Collectors.toList());
    }

    @Bean
    @Qualifier("blockingUserIds")
    public List<Integer> getBlockingUserIds(SettingRepository settingRepository, User systemUser) {
        return settingRepository.getByUserAndSettingType(systemUser, SettingType.BLOCKING_USER_ID)
                .stream().map(PropertySetting::getProperty).collect(Collectors.toList());
    }

    @Bean
    @Qualifier("docFilter")
    public DocFilter getDocFilter() {
        DocFilter postingFilter = new PostingFilter();
        DocFilter storeFilter = new StoreFilter();
        DocFilter requestFilter = new RequestFilter();
        DocFilter orderFilter = new OrderFilter();
        DocFilter checkFilter = new CheckFilter();
        DocFilter inventFilter = new InventFilter();
        DocFilter defaultFilter = new DefaultFilter();

        postingFilter.setNext(storeFilter);
        storeFilter.setNext(requestFilter);
        requestFilter.setNext(orderFilter);
        orderFilter.setNext(checkFilter);
        checkFilter.setNext(inventFilter);
        inventFilter.setNext(defaultFilter);

        return postingFilter;
    }

}
