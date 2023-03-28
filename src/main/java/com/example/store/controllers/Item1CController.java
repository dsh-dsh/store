package com.example.store.controllers;

import com.example.store.model.dto.Item1CDTO;
import com.example.store.model.dto.requests.CompanyList1CRequestDTO;
import com.example.store.model.dto.requests.ItemList1CRequestDTO;
import com.example.store.model.dto.requests.UserList1CRequestDTO;
import com.example.store.model.responses.ListResponse;
import com.example.store.model.responses.Response;
import com.example.store.services.Company1CService;
import com.example.store.services.Item1CService;
import com.example.store.services.User1CService;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Item1CController {

    @Autowired
    private Item1CService item1CService;
    @Autowired
    private User1CService user1CService;
    @Autowired
    private Company1CService company1CService;

//    @GetMapping("/items") //(value = "/items", produces = "application/json; charset=windows-1252")
//    public ResponseEntity<String> getItems() {
//        List<ItemDTO> list = item1CService.getItemDTOList();
//        ListResponse<ItemDTO> response = new ListResponse<>(list, list.size());
//        return ResponseEntity.ok(response.toJsonString());
//    }

    @GetMapping("/items")
    public ResponseEntity<ListResponse<Item1CDTO>> getItems() {
        List<Item1CDTO> list = item1CService.getItemDTOList();
        return ResponseEntity.ok(new ListResponse<>(list, list.size()));
    }


    @PostMapping("/items")
    public ResponseEntity<Response<String>>  setItems(
            @RequestBody ItemList1CRequestDTO itemList1CRequestDTO) {
        item1CService.setItemsFrom1C(itemList1CRequestDTO);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PostMapping("/users")
    public ResponseEntity<Response<String>>  setUsers(
            @RequestBody UserList1CRequestDTO userList1CRequestDTO) {
        user1CService.setUsersFrom1C(userList1CRequestDTO);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PostMapping("/companies")
    public ResponseEntity<Response<String>>  setCompanies(
            @RequestBody CompanyList1CRequestDTO companyList1CRequestDTO) {
        company1CService.setCompaniesFrom1C(companyList1CRequestDTO);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

}
