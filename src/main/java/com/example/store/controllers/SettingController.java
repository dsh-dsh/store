package com.example.store.controllers;

import com.example.store.model.dto.SettingDTO;
import com.example.store.model.responses.ListResponse;
import com.example.store.model.responses.Response;
import com.example.store.services.SettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/setting")
public class SettingController {

    @Autowired
    private SettingService settingService;

    @GetMapping
    public ResponseEntity<ListResponse<SettingDTO>> getSettings(
            @RequestParam(defaultValue = "") int userId) {
        return ResponseEntity.ok(settingService.getSettingsByUser(userId));
    }

    @GetMapping("/add/shortage")
    public ResponseEntity<Response<SettingDTO>> getSettings() {
        return ResponseEntity.ok(settingService.getAddShortageForHoldSetting());
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

}
