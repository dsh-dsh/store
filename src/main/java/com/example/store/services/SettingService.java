package com.example.store.services;

import com.example.store.model.dto.SettingDTO;
import com.example.store.model.dto.UserDTO;
import com.example.store.model.entities.DefaultPropertySetting;
import com.example.store.model.entities.User;
import com.example.store.model.responses.ListResponse;
import com.example.store.repositories.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SettingService {

    @Autowired
    private SettingRepository settingRepository;
    @Autowired
    private UserService userService;

    public ListResponse<SettingDTO> getSettingsByUser(int userId) {
        User user = userService.getById(userId);
        List<DefaultPropertySetting> list = settingRepository.findByUser(user);
        UserDTO userDTO = new UserDTO(user.getId(), user.getEmail(), "");
        List<SettingDTO> dtoList = list.stream()
                .map(setting -> getSettingDTO(setting, userDTO))
                .collect(Collectors.toList());
        return new ListResponse<>(dtoList);
    }

    public SettingDTO getSettingDTO(DefaultPropertySetting setting, UserDTO userDTO) {
        SettingDTO dto = new SettingDTO();
        dto.setUser(userDTO);
        dto.setType(setting.getSettingType().toString());
        dto.setProperty(setting.getProperty());
        return dto;
    }
}
