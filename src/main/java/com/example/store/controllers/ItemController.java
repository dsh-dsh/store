package com.example.store.controllers;

import com.example.store.model.dto.ItemDTO;
import com.example.store.model.dto.ItemDTOForList;
import com.example.store.model.dto.ItemDTOForTree;
import com.example.store.model.entities.Item;
import com.example.store.model.responses.ListResponse;
import com.example.store.model.responses.Response;
import com.example.store.services.ItemService;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("/tree")
    public ResponseEntity<ListResponse<ItemDTOForTree>> getItemTree() {
        List<ItemDTOForTree> list = itemService.getItemDTOTree();
        return ResponseEntity.ok(new ListResponse<>(list));
    }

    @GetMapping
    public ResponseEntity<Response<ItemDTO>> getItemById(
            @RequestParam String date,
            @RequestParam int id) {
        ItemDTO itemDTO = itemService.getItemDTOById(id, date);
        return ResponseEntity.ok(new Response<>(itemDTO));
    }

    @PostMapping
    public ResponseEntity<Response<Integer>> getItemById(@RequestBody ItemDTO itemDTO) {
        Item item = itemService.setNewItem(itemDTO);
        return ResponseEntity.ok(new Response<>(item.getId()));
    }

    @PutMapping("/{date}")
    public ResponseEntity<Response<String>> updateItemById(
            @PathVariable String date,
            @RequestBody ItemDTO itemDTO) {
        itemService.updateItem(itemDTO, date);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> softDeleteItemById(@PathVariable int id) {
        itemService.softDeleteItem(id);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

}
