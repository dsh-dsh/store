package com.example.store.components;

import com.example.store.model.entities.PropertySetting;
import com.example.store.model.entities.User;
import com.example.store.model.enums.SettingType;
import com.example.store.repositories.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;

@Component
public class SystemSettingsCash {

    @Autowired
    private SettingRepository settingRepository;
    @Autowired
    private User systemUser;

    private static final int DEFAULT_VALUE = 1;
    private final EnumMap<SettingType, PropertySetting> cash = new EnumMap<>(SettingType.class);

    public PropertySetting getSetting(SettingType type) {
        cash.computeIfAbsent(type, this::getSettingByType);
        return cash.get(type);
    }

    public int getProperty(SettingType type) {
        return getSetting(type).getProperty();
    }

    public void setSetting(SettingType type, int value) {
        PropertySetting setting = settingRepository.findByUserAndSettingType(systemUser, type)
                .orElseGet(() -> PropertySetting.of(type, systemUser, value));
        setting.setProperty(value);
        settingRepository.save(setting);
        cash.put(type, setting);
    }

    private PropertySetting getSettingByType(SettingType type) {
        return settingRepository.findByUserAndSettingType(systemUser, type)
                .orElseGet(() -> PropertySetting.of(type, systemUser, DEFAULT_VALUE));
    }
}
