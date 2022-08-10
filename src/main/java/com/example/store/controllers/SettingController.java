package com.example.store.controllers;

import com.example.store.model.dto.PeriodDTO;
import com.example.store.model.dto.SettingDTO;
import com.example.store.model.responses.ListResponse;
import com.example.store.model.responses.Response;
import com.example.store.services.PeriodService;
import com.example.store.services.SettingService;
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
        return ResponseEntity.ok(settingService.getSettingsByUser(userId));
    }

    @GetMapping("/add/shortage")
    public ResponseEntity<Response<SettingDTO>> getHoldingSettings() {
        return ResponseEntity.ok(settingService.getAddShortageForHoldSetting());
    }

    @GetMapping("/average/price/period")
    public ResponseEntity<Response<SettingDTO>> getAveragePriceForPeriodCloseSettings() {
        return ResponseEntity.ok(settingService.getAveragePriceForPeriodCloseSettings());
    }

    @GetMapping("/average/price/docs")
    public ResponseEntity<Response<SettingDTO>> getAveragePriceForDocsSettings() {
        return ResponseEntity.ok(settingService.getAveragePriceForDocsSettings());
    }

    @GetMapping("/period")
    public ResponseEntity<Response<PeriodDTO>> getCurrentPeriod() {
        return ResponseEntity.ok(new Response<>(periodService.getPeriodDTO()));
    }

    @PostMapping("/property")
    public ResponseEntity<Response<String>> setSettings(@RequestBody SettingDTO settingDTO) {
        settingService.setProperty(settingDTO);
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PostMapping("/add/shortage")
    public ResponseEntity<Response<String>> setAddShortageSetting(@RequestBody SettingDTO settingDTO) {
        settingService.setAddShortageSetting(settingDTO);
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PostMapping("/average/price/period")
    public ResponseEntity<Response<String>> setAveragePriceForPeriodCloseSetting(@RequestBody SettingDTO settingDTO) {
        settingService.setAveragePriceForPeriodCloseSetting(settingDTO);
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PostMapping("/average/price/docs")
    public ResponseEntity<Response<String>> setAveragePriceForDocsSetting(@RequestBody SettingDTO settingDTO) {
        settingService.setAveragePriceForDocsSetting(settingDTO);
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PostMapping("/period")
    public ResponseEntity<Response<PeriodDTO>> closePeriod() {
        PeriodDTO periodDTO = periodService.closePeriodManually();
        return ResponseEntity.ok(new Response<>(periodDTO));
    }

}
