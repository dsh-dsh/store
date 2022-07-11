package com.example.store.repositories;

import com.example.store.model.entities.DefaultPropertySetting;
import com.example.store.model.entities.User;
import com.example.store.model.enums.SettingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SettingRepository extends JpaRepository<DefaultPropertySetting, Integer> {

    List<DefaultPropertySetting> findByUser(User user);

    Optional<DefaultPropertySetting> findByUserAndSettingType(User user, SettingType settingType);
}
