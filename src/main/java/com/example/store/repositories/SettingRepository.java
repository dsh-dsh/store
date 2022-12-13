package com.example.store.repositories;

import com.example.store.model.entities.PropertySetting;
import com.example.store.model.entities.User;
import com.example.store.model.enums.SettingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<PropertySetting, Integer> {

    List<PropertySetting> findByUser(User user);

    Optional<PropertySetting> findByUserAndSettingType(User user, SettingType settingType);

    List<PropertySetting> getByUserAndSettingType(User user, SettingType settingType);

    void deleteByUserAndSettingType(User user, SettingType settingType);

    @Transactional
    @Modifying
    @Query(value =
            "update default_property_setting set property = 0 " +
            "where user_id = :userId and setting_type like '%DOC_TYPE_FILTER'", nativeQuery = true)
    void resetDocTypeFiltersSettings(int userId);
}
