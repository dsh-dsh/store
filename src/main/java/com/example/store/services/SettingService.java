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
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Autowired
    @Qualifier("addRestForHold")
    protected PropertySetting addRestForHoldSetting;
    @Autowired
    @Qualifier("periodAveragePrice")
    private PropertySetting periodAveragePriceSetting;
    @Autowired
    @Qualifier("docsAveragePrice")
    private PropertySetting docsAveragePriceSetting;
    @Autowired
    @Qualifier("ourCompany")
    private PropertySetting ourCompanySetting;
    @Autowired
    @Qualifier("ingredientDir")
    private PropertySetting ingredientDirSetting;

    public PropertySetting getSettingByType(User user, SettingType type) {
        Optional<PropertySetting> setting = settingRepository.findByUserAndSettingType(user, type);
        return setting.orElse(null);
    }

    public ListResponse<SettingDTO> getSettingsByUser(int userId) {
        User user = userService.getById(userId);
        List<PropertySetting> list = settingRepository.findByUser(user);
        UserDTO userDTO = new UserDTO(user.getId(), user.getEmail(), "");
        List<SettingDTO> dtoList = list.stream()
                .filter(setting -> setting.getSettingType() != SettingType.ADD_REST_FOR_HOLD_1C_DOCS)
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
        setAddShortageSetting(settingDTO.getProperty());
    }

    public void setAddShortageSetting(int property) {
        addRestForHoldSetting.setProperty(property);
        SettingType settingType = SettingType.ADD_REST_FOR_HOLD_1C_DOCS;
        PropertySetting setting = settingRepository.findByUserAndSettingType(systemUser, settingType)
                .orElseGet(() -> getSetting(systemUser, settingType, property));
        setting.setProperty(property);
        settingRepository.save(setting);
    }

    public void setAveragePriceForPeriodCloseSetting(SettingDTO settingDTO) {
        setAveragePriceForPeriodCloseSetting(settingDTO.getProperty());
    }

    public void setAveragePriceForPeriodCloseSetting(int property) {
        periodAveragePriceSetting.setProperty(property);
        SettingType settingType = SettingType.PERIOD_AVERAGE_PRICE;
        PropertySetting setting = settingRepository.findByUserAndSettingType(systemUser, settingType)
                .orElseGet(() -> getSetting(systemUser, settingType, property));
        setting.setProperty(property);
        settingRepository.save(setting);
    }

    public void setAveragePriceForDocsSetting(SettingDTO settingDTO) {
        setAveragePriceForDocsSetting(settingDTO.getProperty());
    }

    public void setAveragePriceForDocsSetting(int property) {
        docsAveragePriceSetting.setProperty(property);
        SettingType settingType = SettingType.DOCS_AVERAGE_PRICE;
        PropertySetting setting = settingRepository.findByUserAndSettingType(systemUser, settingType)
                .orElseGet(() -> getSetting(systemUser, settingType, property));
        setting.setProperty(property);
        settingRepository.save(setting);
    }

    public void setOurCompanySetting(SettingDTO settingDTO) {
        ourCompanySetting.setProperty(settingDTO.getProperty());
        setSystemSetting(settingDTO, SettingType.OUR_COMPANY_ID);
    }

    public void setIngredientDirSetting(SettingDTO settingDTO) {
        ingredientDirSetting.setProperty(settingDTO.getProperty());
        setSystemSetting(settingDTO, SettingType.INGREDIENT_DIR_ID);
    }

    public void setSystemSetting(SettingDTO dto, SettingType settingType) {
        PropertySetting setting = settingRepository.findByUserAndSettingType(systemUser, settingType)
                .orElseGet(() -> getSetting(systemUser, settingType, dto.getProperty()));
        setting.setProperty(dto.getProperty());
        settingRepository.save(setting);
    }



    private PropertySetting getSetting(User user, SettingType type, int property) {
        PropertySetting setting = new PropertySetting();
        setting.setUser(user);
        setting.setSettingType(type);
        setting.setProperty(property);
        return  setting;
    }

    public SettingDTO getAddShortageForHoldSetting() {
        UserDTO userDTO = new UserDTO(systemUser.getId(), systemUser.getEmail(), "");
        return getSettingDTO(addRestForHoldSetting, userDTO);
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

    public Response<SettingDTO> getOurCompanySettings() {
        UserDTO userDTO = new UserDTO(systemUser.getId(), systemUser.getEmail(), "");
        return new Response<>(getSettingDTO(ourCompanySetting, userDTO));
    }

    public Response<SettingDTO> getIngredientDirSettings() {
        SettingType settingType = SettingType.INGREDIENT_DIR_ID;
        PropertySetting setting = settingRepository.findByUserAndSettingType(systemUser, settingType)
                .orElseGet(() -> getSetting(systemUser, settingType, 1));
        UserDTO userDTO = new UserDTO(systemUser.getId(), systemUser.getEmail(), "");
        return new Response<>(getSettingDTO(setting, userDTO));
    }

    public PropertySetting getSystemSetting(SettingType settingType) {
        return settingRepository.findByUserAndSettingType(systemUser, settingType)
                .orElseGet(() -> getSetting(systemUser, settingType, 1));
    }
}
