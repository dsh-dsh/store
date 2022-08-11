package com.example.store.configuration;

import com.example.store.model.entities.DefaultPropertySetting;
import com.example.store.model.entities.User;
import com.example.store.repositories.SettingRepository;
import com.example.store.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class Config {

    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

    @Bean
    public List<DefaultPropertySetting> getSystemSettings(SettingRepository settingRepository, UserService userService) {
        User systemUser = userService.getSystemAuthor();
        return settingRepository.findByUser(systemUser);
    }

}
