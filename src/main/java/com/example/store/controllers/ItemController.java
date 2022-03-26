package com.example.store.controllers;

import com.example.store.model.dto.ItemDTO;
import com.example.store.model.responses.Response;
import com.example.store.services.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/api/v1/items")
    public ResponseEntity<Response<ItemDTO>> getItemById(@RequestParam int id) {
        ItemDTO itemDTO = itemService.getItemDTOById(id);
        return ResponseEntity.ok(new Response<>(itemDTO));
    }

    @PostMapping("/api/v1/items")
    public ResponseEntity<Response<Integer>> getItemById(@RequestBody ItemDTO itemDTO) {
        int itemId = itemService.setNewItem(itemDTO);
        return ResponseEntity.ok(new Response<>(itemId));
    }

}
