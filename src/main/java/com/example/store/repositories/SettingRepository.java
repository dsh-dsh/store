package com.example.store.repositories;

import com.example.store.model.entities.PropertySetting;
import com.example.store.model.entities.User;
import com.example.store.model.enums.SettingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SettingRepository extends JpaRepository<PropertySetting, Integer> {

    List<PropertySetting> findByUser(User user);

    Optional<PropertySetting> findByUserAndSettingType(User user, SettingType settingType);
}
