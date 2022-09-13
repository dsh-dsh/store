package com.example.store.controllers;

import com.example.store.model.dto.requests.ItemList1CRequestDTO;
import com.example.store.model.dto.requests.UserList1CRequestDTO;
import com.example.store.model.responses.Response;
import com.example.store.services.Item1CService;
import com.example.store.services.User1CService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Item1CController {

    @Autowired
    private Item1CService item1CService;
    @Autowired
    private User1CService user1CService;

    @PostMapping("/items")
    public ResponseEntity<Response<String>>  setItems(
            @RequestBody ItemList1CRequestDTO itemList1CRequestDTO) {
        item1CService.setItemsFrom1C(itemList1CRequestDTO);
        return ResponseEntity.ok(new Response<>("ok"));
    }

    // todo add tests
    @PostMapping("/users")
    public ResponseEntity<Response<String>>  setUsers(
            @RequestBody UserList1CRequestDTO userList1CRequestDTO) {
        user1CService.setUsersFrom1C(userList1CRequestDTO);
        return ResponseEntity.ok(new Response<>("ok"));
    }

}
