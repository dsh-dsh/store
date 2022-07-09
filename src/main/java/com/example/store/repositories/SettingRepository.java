package com.example.store.repositories;

import com.example.store.model.entities.DefaultPropertySetting;
import com.example.store.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettingRepository extends JpaRepository<DefaultPropertySetting, Integer> {

    List<DefaultPropertySetting> findByUser(User user);

}
