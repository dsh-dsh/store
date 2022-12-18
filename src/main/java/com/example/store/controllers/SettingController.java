package com.example.store.controllers;

import com.example.store.model.dto.PeriodDTO;
import com.example.store.model.dto.SettingDTO;
import com.example.store.model.dto.SettingDTOList;
import com.example.store.model.dto.requests.IdsDTO;
import com.example.store.model.enums.SettingType;
import com.example.store.model.responses.ListResponse;
import com.example.store.model.responses.Response;
import com.example.store.services.PeriodService;
import com.example.store.services.SettingService;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/setting")
public class SettingController {

    @Autowired
    private SettingService settingService;
    @Autowired
    private PeriodService periodService;

    @GetMapping
    public ResponseEntity<ListResponse<SettingDTO>> getSettings(
            @RequestParam(defaultValue = "") int userId) {
        return ResponseEntity.ok(new ListResponse<>(settingService.getSettingsByUser(userId)));
    }

    @GetMapping("/system")
    public ResponseEntity<Response<SettingDTO>> getSystemSetting(
            @RequestParam(defaultValue = "") String type) {
        return ResponseEntity.ok(new Response<>(settingService.getSystemSettingDTO(type)));
    }

    @GetMapping("/system/all")
    public ResponseEntity<ListResponse<SettingDTO>> getAllSystemSettings() {
        return ResponseEntity.ok(new ListResponse<>(settingService.getAllSystemSettings()));
    }

    @GetMapping("/period")
    public ResponseEntity<Response<PeriodDTO>> getCurrentPeriod() {
        return ResponseEntity.ok(new Response<>(periodService.getPeriodDTO()));
    }

    @GetMapping("/disabled/items")
    public ResponseEntity<Response<IdsDTO>> getDisabledItems() {
        return ResponseEntity.ok(new Response<>(
                settingService.getIdSettingList(SettingType.DISABLED_ITEM_ID)));
    }

    @GetMapping("/blocking/users")
    public ResponseEntity<Response<IdsDTO>> getBlockingUsers() {
        return ResponseEntity.ok(new Response<>(
                settingService.getIdSettingList(SettingType.BLOCKING_USER_ID)));
    }

    @GetMapping("/block/time")
    public ResponseEntity<Response<Long>> getBlockTime() {
        return ResponseEntity.ok(new Response<>(periodService.getBlockTime()));
    }

    @PostMapping("/property")
    public ResponseEntity<Response<String>> setProperty(@RequestBody SettingDTO settingDTO) {
        settingService.setProperty(settingDTO);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PostMapping("/system/property")
    public ResponseEntity<Response<String>> setSystemProperty(@RequestBody SettingDTO settingDTO) {
        settingService.setSystemProperty(settingDTO);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PostMapping("/doc/type/properties")
    public ResponseEntity<Response<String>> setDocTypeFilterProperties(@RequestBody SettingDTOList settingDTOList) {
        settingService.setDocTypeFilterProperties(settingDTOList);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PostMapping("/period")
    public ResponseEntity<Response<PeriodDTO>> closePeriod() {
        PeriodDTO periodDTO = periodService.closePeriodManually();
        return ResponseEntity.ok(new Response<>(periodDTO));
    }

    @PostMapping("/disabled/items")
    public ResponseEntity<Response<String>> setDisabledItems(@RequestBody IdsDTO itemIds) {
        settingService.setIdSettingList(itemIds, SettingType.DISABLED_ITEM_ID);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PostMapping("/blocking/users")
    public ResponseEntity<Response<String>> setBlockingUses(@RequestBody IdsDTO itemIds) {
        settingService.setIdSettingList(itemIds, SettingType.BLOCKING_USER_ID);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

}
