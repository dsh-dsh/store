package com.example.store.controllers;

import com.example.store.model.dto.requests.ItemList1CRequestDTO;
import com.example.store.model.responses.Response;
import com.example.store.services.Item1CService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Item1CController {

    @Autowired
    private Item1CService item1CService;

    @PostMapping("/items")
    public ResponseEntity<Response<String>>  setItems(
            @RequestBody ItemList1CRequestDTO itemList1CRequestDTO) {
        item1CService.setItemsFrom1C(itemList1CRequestDTO);
        return ResponseEntity.ok(new Response<>("ok"));
    }

}
