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

    @GetMapping("/add/shortage")
    public ResponseEntity<Response<SettingDTO>> getAddShortageForHoldSetting() {
        return ResponseEntity.ok(new Response<>(settingService.getAddShortageForHoldSetting()));
    }

    @GetMapping("/average/price/period")
    public ResponseEntity<Response<SettingDTO>> getAveragePriceForPeriodCloseSetting() {
        return ResponseEntity.ok(new Response<>(settingService.getAveragePriceForPeriodCloseSetting()));
    }

    @GetMapping("/average/price/docs")
    public ResponseEntity<Response<SettingDTO>> getAveragePriceForDocsSetting() {
        return ResponseEntity.ok(new Response<>(settingService.getAveragePriceForDocsSetting()));
    }

    @GetMapping("/our/company")
    public ResponseEntity<Response<SettingDTO>> getOurCompanySetting() {
        return ResponseEntity.ok(new Response<>(settingService.getOurCompanySetting()));
    }

    @GetMapping("/ingredient/dir")
    public ResponseEntity<Response<SettingDTO>> getIngredientDirSetting() {
        return ResponseEntity.ok(new Response<>(settingService.getIngredientDirSetting()));
    }

    @GetMapping("/hold/dialog/enable")
    public ResponseEntity<Response<SettingDTO>> getHoldingDialogEnableSetting() {
        return ResponseEntity.ok(new Response<>(settingService.getHoldingDialogEnableSetting()));
    }

    @GetMapping("/check/holding/enable")
    public ResponseEntity<Response<SettingDTO>> getCheckHoldingEnableSetting() {
        return ResponseEntity.ok(new Response<>(settingService.getCheckHoldingEnableSetting()));
    }

    // todo add tests
    @GetMapping("/doc/block/enable")
    public ResponseEntity<Response<SettingDTO>> getEnableDocsBlockSetting() {
        return ResponseEntity.ok(new Response<>(settingService.getEnableDocsBlockSetting()));
    }

    @GetMapping("/period")
    public ResponseEntity<Response<PeriodDTO>> getCurrentPeriod() {
        return ResponseEntity.ok(new Response<>(periodService.getPeriodDTO()));
    }

    // todo add tests
    @GetMapping("disabled/items")
    public ResponseEntity<Response<IdsDTO>> getDisabledItems() {
        return ResponseEntity.ok(new Response<>(
                settingService.getIdSettingList(SettingType.DISABLED_ITEM_ID)));
    }

    // todo add tests
    @GetMapping("blocking/users")
    public ResponseEntity<Response<IdsDTO>> getBlockingUsers() {
        return ResponseEntity.ok(new Response<>(
                settingService.getIdSettingList(SettingType.BLOCKING_USER_ID)));
    }

    @GetMapping("/block/time")
    public ResponseEntity<Response<Long>> getBlockTime() {
        return ResponseEntity.ok(new Response<>(periodService.getBlockTime()));
    }

    @PostMapping("/property")
    public ResponseEntity<Response<String>> setSetting(@RequestBody SettingDTO settingDTO) {
        settingService.setProperty(settingDTO);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PostMapping("/doc/type/properties")
    public ResponseEntity<Response<String>> setDocTypeFilterProperties(@RequestBody SettingDTOList settingDTOList) {
        settingService.setDocTypeFilterProperties(settingDTOList);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PostMapping("/add/shortage")
    public ResponseEntity<Response<String>> setAddShortageSetting(@RequestBody SettingDTO settingDTO) {
        settingService.setAddShortageSetting(settingDTO);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PostMapping("/average/price/period")
    public ResponseEntity<Response<String>> setAveragePriceForPeriodCloseSetting(@RequestBody SettingDTO settingDTO) {
        settingService.setAveragePriceForPeriodCloseSetting(settingDTO);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PostMapping("/average/price/docs")
    public ResponseEntity<Response<String>> setAveragePriceForDocsSetting(@RequestBody SettingDTO settingDTO) {
        settingService.setAveragePriceForDocsSetting(settingDTO);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PostMapping("/our/company")
    public ResponseEntity<Response<String>> setOurCompanySetting(@RequestBody SettingDTO settingDTO) {
        settingService.setOurCompanySetting(settingDTO);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PostMapping("/ingredient/dir")
    public ResponseEntity<Response<String>> setIngredientDirSetting(@RequestBody SettingDTO settingDTO) {
        settingService.setIngredientDirSetting(settingDTO);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PostMapping("/hold/dialog/enable")
    public ResponseEntity<Response<String>> setHoldDialogEnableSetting(@RequestBody SettingDTO settingDTO) {
        settingService.setHoldingDialogEnableSetting(settingDTO);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PostMapping("/check/holding/enable")
    public ResponseEntity<Response<String>> setCheckHoldingEnableSetting(@RequestBody SettingDTO settingDTO) {
        settingService.setCheckHoldingEnableSetting(settingDTO);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    // todo add tests
    @PostMapping("/doc/block/enable")
    public ResponseEntity<Response<String>> setEnableDocsBlockSetting(@RequestBody SettingDTO settingDTO) {
        settingService.setEnableDocsBlockSetting(settingDTO);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    // todo add tests
    @PostMapping("/period")
    public ResponseEntity<Response<PeriodDTO>> closePeriod() {
        PeriodDTO periodDTO = periodService.closePeriodManually();
        return ResponseEntity.ok(new Response<>(periodDTO));
    }

    // todo add tests
    @PostMapping("disabled/items")
    public ResponseEntity<Response<String>> setDisabledItems(@RequestBody IdsDTO itemIds) {
        settingService.setIdSettingList(itemIds, SettingType.DISABLED_ITEM_ID);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    // todo add tests
    @PostMapping("blocking/users")
    public ResponseEntity<Response<String>> setBlockingUses(@RequestBody IdsDTO itemIds) {
        settingService.setIdSettingList(itemIds, SettingType.BLOCKING_USER_ID);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

}
