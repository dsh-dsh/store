package com.example.store.services;

import com.example.store.components.SystemSettingsCash;
import com.example.store.model.dto.SettingDTO;
import com.example.store.model.dto.SettingDTOList;
import com.example.store.model.dto.UserDTO;
import com.example.store.model.dto.requests.IdsDTO;
import com.example.store.model.entities.PropertySetting;
import com.example.store.model.entities.User;
import com.example.store.model.enums.SettingType;
import com.example.store.repositories.SettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    @Autowired
    private SystemSettingsCash systemSettingsCash;
    @Autowired
    @Qualifier("disabledItemIds")
    protected List<Integer> disabledItemIds;
    @Autowired
    @Qualifier("blockingUserIds")
    protected List<Integer> blockingUserIds;

    public PropertySetting getSystemSetting(SettingType type) {
        return systemSettingsCash.getSetting(type);
    }

    public SettingDTO getSystemSettingDTO(String type) {
        return mapToDTO(getSystemSetting(SettingType.valueOf(type)), null);
    }

    public PropertySetting getSettingByType(User user, SettingType type) {
        Optional<PropertySetting> setting = settingRepository.findByUserAndSettingType(user, type);
        return setting.orElse(null);
    }

    // todo add tests
    public List<SettingDTO> getAllSystemSettings() {
        List<SettingDTO> list = new ArrayList<>();
        list.add(getSystemSettingDTO(SettingType.ADD_REST_FOR_HOLD_1C_DOCS.toString()));
        list.add(getSystemSettingDTO(SettingType.PERIOD_AVERAGE_PRICE.toString()));
        list.add(getSystemSettingDTO(SettingType.DOCS_AVERAGE_PRICE.toString()));
        list.add(getSystemSettingDTO(SettingType.INGREDIENT_DIR_ID.toString()));
        list.add(getSystemSettingDTO(SettingType.OUR_COMPANY_ID.toString()));
        list.add(getSystemSettingDTO(SettingType.HOLDING_DIALOG_ENABLE.toString()));
        list.add(getSystemSettingDTO(SettingType.DOC_BLOCK_ENABLE.toString()));
        list.add(getSystemSettingDTO(SettingType.CHECK_HOLDING_ENABLE.toString()));
        return list;
    }

    public List<SettingDTO> getSettingsByUser(int userId) {
        User user = userService.getById(userId);
        List<PropertySetting> list = settingRepository.findByUser(user);
        UserDTO userDTO = new UserDTO(user.getId(), user.getEmail(), "");
        return list.stream()
                .map(setting -> mapToDTO(setting, userDTO))
                .collect(Collectors.toList());
    }

    public SettingDTO mapToDTO(PropertySetting setting, UserDTO userDTO) {
        SettingDTO dto = new SettingDTO();
        dto.setUser(userDTO);
        dto.setType(setting.getSettingType().toString());
        dto.setProperty(setting.getProperty());
        return dto;
    }

    public void setDocTypeFilterProperties(SettingDTOList list) {
        User user = userService.getById(list.getUser().getId());
        settingRepository.resetDocTypeFiltersSettings(list.getUser().getId());
        list.getSettings().forEach(dto -> setProperty(user, dto));
    }

    public void setProperty(User user, SettingDTO settingDTO) {
        SettingType settingType = SettingType.valueOf(settingDTO.getType());
        int property = settingDTO.getProperty();
        PropertySetting setting = settingRepository.findByUserAndSettingType(user, settingType)
                .orElseGet(() -> getNewSetting(user, settingType, property));
        setting.setProperty(property);
        settingRepository.save(setting);
    }

    public void setProperty(SettingDTO settingDTO) {
        User user = userService.getById(settingDTO.getUser().getId());
        setProperty(user, settingDTO);
    }

    public void setSystemProperty(SettingDTO dto) {
        systemSettingsCash.setSetting(SettingType.valueOf(dto.getType()), dto.getProperty());
    }

    // todo add tests
    @Transactional
    public void setIdSettingList(IdsDTO idsDTO, SettingType type) {
        settingRepository.deleteByUserAndSettingType(systemUser, type);
        for(int id : idsDTO.getIds()) {
            settingRepository.save(PropertySetting.of(type, systemUser, id));
        }
        updateIdsSettingBean(type, idsDTO.getIds());
    }

    // todo add tests
    protected void updateIdsSettingBean(SettingType type, List<Integer> ids) {
        if(type == SettingType.BLOCKING_USER_ID) {
            blockingUserIds.clear();
            blockingUserIds.addAll(ids);
        } else {
            disabledItemIds.clear();
            disabledItemIds.addAll(ids);
        }
    }

    // todo add tests
    public IdsDTO getIdSettingList(SettingType type) {
        List<PropertySetting> settings = settingRepository.getByUserAndSettingType(systemUser, type);
        return new IdsDTO(settings.stream().map(PropertySetting::getProperty).collect(Collectors.toList()));
    }

    private PropertySetting getNewSetting(User user, SettingType type, int property) {
        PropertySetting setting = new PropertySetting();
        setting.setUser(user);
        setting.setSettingType(type);
        setting.setProperty(property);
        return  setting;
    }
}
