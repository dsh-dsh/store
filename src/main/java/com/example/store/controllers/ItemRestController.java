package com.example.store.controllers;

import com.example.store.model.dto.DocItemDTO;
import com.example.store.model.responses.ListResponse;
import com.example.store.services.ItemRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rest")
public class ItemRestController {

    @Autowired
    private ItemRestService itemRestService;

    @GetMapping("/inventory")
    public ResponseEntity<ListResponse<DocItemDTO>> getItemRest(
            @RequestParam(defaultValue = "") String time,
            @RequestParam(defaultValue = "0") int storageId) {
        ListResponse<DocItemDTO> response =
                new ListResponse<>(itemRestService.getItemRest(time, storageId));
        return ResponseEntity.ok(response);
    }

}
