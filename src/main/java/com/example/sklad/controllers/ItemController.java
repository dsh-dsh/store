package com.example.sklad.controllers;

import com.example.sklad.model.dto.ItemDTO;
import com.example.sklad.model.responses.Response;
import com.example.sklad.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/api/v1/items")
    public ResponseEntity<Response<ItemDTO>> getItemById(@RequestParam int id) {
        ItemDTO itemDTO = itemService.getItemDTOById(id);
        return ResponseEntity.ok(new Response<>(itemDTO));
    }

}
