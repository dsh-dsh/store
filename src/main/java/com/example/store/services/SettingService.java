package com.example.store.services;

import com.example.store.model.dto.SettingDTO;
import com.example.store.model.dto.UserDTO;
import com.example.store.model.entities.DefaultPropertySetting;
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

    public DefaultPropertySetting getSettingByType(User user, SettingType type) {
        Optional<DefaultPropertySetting> setting = settingRepository.findByUserAndSettingType(user, type);
        return setting.orElse(null);
    }

    public ListResponse<SettingDTO> getSettingsByUser(int userId) {
        User user = userService.getById(userId);
        List<DefaultPropertySetting> list = settingRepository.findByUser(user);
        UserDTO userDTO = new UserDTO(user.getId(), user.getEmail(), "");
        List<SettingDTO> dtoList = list.stream()
                .filter(setting -> setting.getSettingType() != SettingType.ADD_REST_FOR_HOLD)
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

    public void setProperty(SettingDTO settingDTO) {
        User user = userService.getById(settingDTO.getUser().getId());
        SettingType settingType = SettingType.valueOf(settingDTO.getType());
        int property = settingDTO.getProperty();
        DefaultPropertySetting setting = settingRepository.findByUserAndSettingType(user, settingType)
                .orElseGet(() -> getSetting(user, settingType, property));
        setting.setProperty(property);
        settingRepository.save(setting);
    }

    public void setAddShortageSetting(SettingDTO settingDTO) {
        User user = userService.getSystemAuthor();
        SettingType settingType = SettingType.ADD_REST_FOR_HOLD;
        int property = settingDTO.getProperty();
        DefaultPropertySetting setting = settingRepository.findByUserAndSettingType(user, settingType)
                .orElseGet(() -> getSetting(user, settingType, property));
        setting.setProperty(property);
        settingRepository.save(setting);
    }

    public void setAveragePriceForPeriodCloseSetting(SettingDTO settingDTO) {
        User user = userService.getSystemAuthor();
        SettingType settingType = SettingType.PERIOD_AVERAGE_PRICE;
        int property = settingDTO.getProperty();
        DefaultPropertySetting setting = settingRepository.findByUserAndSettingType(user, settingType)
                .orElseGet(() -> getSetting(user, settingType, property));
        setting.setProperty(property);
        settingRepository.save(setting);
    }

    public void setAveragePriceForDocsSetting(SettingDTO settingDTO) {
        User user = userService.getSystemAuthor();
        SettingType settingType = SettingType.DOCS_AVERAGE_PRICE;
        int property = settingDTO.getProperty();
        DefaultPropertySetting setting = settingRepository.findByUserAndSettingType(user, settingType)
                .orElseGet(() -> getSetting(user, settingType, property));
        setting.setProperty(property);
        settingRepository.save(setting);
    }

    private DefaultPropertySetting getSetting(User user, SettingType type, int property) {
        DefaultPropertySetting setting = new DefaultPropertySetting();
        setting.setUser(user);
        setting.setSettingType(type);
        setting.setProperty(property);
        return  setting;
    }

    public Response<SettingDTO> getAddShortageForHoldSetting() {
        User user = userService.getSystemAuthor();
        SettingType settingType = SettingType.ADD_REST_FOR_HOLD;
        DefaultPropertySetting setting = settingRepository.findByUserAndSettingType(user, settingType)
                .orElseGet(() -> getSetting(user, settingType, 1));
        UserDTO userDTO = new UserDTO(user.getId(), user.getEmail(), "");
        return new Response<>(getSettingDTO(setting, userDTO));
    }

    public Response<SettingDTO> getAveragePriceForPeriodCloseSettings() {
        User user = userService.getSystemAuthor();
        SettingType settingType = SettingType.PERIOD_AVERAGE_PRICE;
        DefaultPropertySetting setting = settingRepository.findByUserAndSettingType(user, settingType)
                .orElseGet(() -> getSetting(user, settingType, 1));
        UserDTO userDTO = new UserDTO(user.getId(), user.getEmail(), "");
        return new Response<>(getSettingDTO(setting, userDTO));
    }

    public Response<SettingDTO> getAveragePriceForDocsSettings() {
        SettingType settingType = SettingType.DOCS_AVERAGE_PRICE;
        User user = userService.getSystemAuthor();
        DefaultPropertySetting setting = settingRepository.findByUserAndSettingType(user, settingType)
                .orElseGet(() -> getSetting(user, settingType, 1));
        UserDTO userDTO = new UserDTO(user.getId(), user.getEmail(), "");
        return new Response<>(getSettingDTO(setting, userDTO));
    }
}
