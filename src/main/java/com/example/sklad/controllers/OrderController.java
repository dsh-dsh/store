package com.example.sklad.controllers;

import com.example.sklad.model.dto.requests.ItemDocRequestDTO;
import com.example.sklad.model.responses.Response;
import com.example.sklad.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/api/v1/orders/rko")
    public ResponseEntity<Response<String>> setWithdrawOrder(@RequestBody ItemDocRequestDTO itemDocRequestDTO) {
        orderService.addRKO(itemDocRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PostMapping("/api/v1/orders/pko")
    public ResponseEntity<Response<String>> setCreditOrder(@RequestBody ItemDocRequestDTO itemDocRequestDTO) {
        orderService.addPKO(itemDocRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PutMapping("/api/v1/orders/rko")
    public ResponseEntity<Response<String>> updateWithdrawOrder(@RequestBody ItemDocRequestDTO itemDocRequestDTO) {
        orderService.updateRKO(itemDocRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PutMapping("/api/v1/orders/pko")
    public ResponseEntity<Response<String>> updateCreditOrder(@RequestBody ItemDocRequestDTO itemDocRequestDTO) {
        orderService.updatePKO(itemDocRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }
}
