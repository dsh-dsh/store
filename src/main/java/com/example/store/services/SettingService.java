package com.example.store.services;

import com.example.store.model.dto.SettingDTO;
import com.example.store.model.dto.UserDTO;
import com.example.store.model.entities.PropertySetting;
import com.example.store.model.entities.User;
import com.example.store.model.enums.SettingType;
import com.example.store.model.responses.ListResponse;
import com.example.store.model.responses.Response;
import com.example.store.repositories.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SettingService {

    @Autowired
    private SettingRepository settingRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private User systemUser;

    public PropertySetting getSettingByType(User user, SettingType type) {
        Optional<PropertySetting> setting = settingRepository.findByUserAndSettingType(user, type);
        return setting.orElse(null);
    }

    public ListResponse<SettingDTO> getSettingsByUser(int userId) {
        User user = userService.getById(userId);
        List<PropertySetting> list = settingRepository.findByUser(user);
        UserDTO userDTO = new UserDTO(user.getId(), user.getEmail(), "");
        List<SettingDTO> dtoList = list.stream()
                .filter(setting -> setting.getSettingType() != SettingType.ADD_REST_FOR_HOLD)
                .map(setting -> getSettingDTO(setting, userDTO))
                .collect(Collectors.toList());
        return new ListResponse<>(dtoList);
    }

    public SettingDTO getSettingDTO(PropertySetting setting, UserDTO userDTO) {
        SettingDTO dto = new SettingDTO();
        dto.setUser(userDTO);
        dto.setType(setting.getSettingType().toString());
        dto.setProperty(setting.getProperty());
        return dto;
    }

    public void setProperty(SettingDTO settingDTO) {
        User user = userService.getById(settingDTO.getUser().getId());
        SettingType settingType = SettingType.valueOf(settingDTO.getType());
        int property = settingDTO.getProperty();
        PropertySetting setting = settingRepository.findByUserAndSettingType(user, settingType)
                .orElseGet(() -> getSetting(user, settingType, property));
        setting.setProperty(property);
        settingRepository.save(setting);
    }

    public void setAddShortageSetting(SettingDTO settingDTO) {
        SettingType settingType = SettingType.ADD_REST_FOR_HOLD;
        int property = settingDTO.getProperty();
        PropertySetting setting = settingRepository.findByUserAndSettingType(systemUser, settingType)
                .orElseGet(() -> getSetting(systemUser, settingType, property));
        setting.setProperty(property);
        settingRepository.save(setting);
    }

    public void setAveragePriceForPeriodCloseSetting(SettingDTO settingDTO) {
        SettingType settingType = SettingType.PERIOD_AVERAGE_PRICE;
        int property = settingDTO.getProperty();
        PropertySetting setting = settingRepository.findByUserAndSettingType(systemUser, settingType)
                .orElseGet(() -> getSetting(systemUser, settingType, property));
        setting.setProperty(property);
        settingRepository.save(setting);
    }

    public void setAveragePriceForDocsSetting(SettingDTO settingDTO) {
        SettingType settingType = SettingType.DOCS_AVERAGE_PRICE;
        int property = settingDTO.getProperty();
        PropertySetting setting = settingRepository.findByUserAndSettingType(systemUser, settingType)
                .orElseGet(() -> getSetting(systemUser, settingType, property));
        setting.setProperty(property);
        settingRepository.save(setting);
    }

    private PropertySetting getSetting(User user, SettingType type, int property) {
        PropertySetting setting = new PropertySetting();
        setting.setUser(user);
        setting.setSettingType(type);
        setting.setProperty(property);
        return  setting;
    }

    public Response<SettingDTO> getAddShortageForHoldSetting() {
        SettingType settingType = SettingType.ADD_REST_FOR_HOLD;
        PropertySetting setting = settingRepository.findByUserAndSettingType(systemUser, settingType)
                .orElseGet(() -> getSetting(systemUser, settingType, 1));
        UserDTO userDTO = new UserDTO(systemUser.getId(), systemUser.getEmail(), "");
        return new Response<>(getSettingDTO(setting, userDTO));
    }

    public Response<SettingDTO> getAveragePriceForPeriodCloseSettings() {
        SettingType settingType = SettingType.PERIOD_AVERAGE_PRICE;
        PropertySetting setting = settingRepository.findByUserAndSettingType(systemUser, settingType)
                .orElseGet(() -> getSetting(systemUser, settingType, 1));
        UserDTO userDTO = new UserDTO(systemUser.getId(), systemUser.getEmail(), "");
        return new Response<>(getSettingDTO(setting, userDTO));
    }

    public Response<SettingDTO> getAveragePriceForDocsSettings() {
        SettingType settingType = SettingType.DOCS_AVERAGE_PRICE;
        PropertySetting setting = settingRepository.findByUserAndSettingType(systemUser, settingType)
                .orElseGet(() -> getSetting(systemUser, settingType, 1));
        UserDTO userDTO = new UserDTO(systemUser.getId(), systemUser.getEmail(), "");
        return new Response<>(getSettingDTO(setting, userDTO));
    }
}
