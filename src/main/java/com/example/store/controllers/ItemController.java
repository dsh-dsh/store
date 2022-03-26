package com.example.store.controllers;

import com.example.store.model.dto.ItemDTO;
import com.example.store.model.entities.Item;
import com.example.store.model.responses.Response;
import com.example.store.services.ItemService;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/api/v1/items")
    public ResponseEntity<Response<ItemDTO>> getItemById(
            @RequestParam String date,
            @RequestParam int id) {
        ItemDTO itemDTO = itemService.getItemDTOById(id, date);
        return ResponseEntity.ok(new Response<>(itemDTO));
    }

    @PostMapping("/api/v1/items")
    public ResponseEntity<Response<Integer>> getItemById(@RequestBody ItemDTO itemDTO) {
        Item item = itemService.setNewItem(itemDTO);
        return ResponseEntity.ok(new Response<>(item.getId()));
    }

    @PutMapping("/api/v1/items/{date}")
    public ResponseEntity<Response<String>> updateItemById(
            @PathVariable String date,
            @RequestBody ItemDTO itemDTO) {
        itemService.updateItem(itemDTO, date);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

}
