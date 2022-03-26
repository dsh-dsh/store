package com.example.store.controllers;

import com.example.store.model.dto.documents.OrderDTO;
import com.example.store.model.dto.requests.DocRequestDTO;
import com.example.store.model.responses.Response;
import com.example.store.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/api/v1/orders")
    public ResponseEntity<Response<OrderDTO>> getOrder(@RequestParam int id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(new Response<>(order));
    }

    @PostMapping("/api/v1/orders/rko")
    public ResponseEntity<Response<String>> setWithdrawOrder(@RequestBody DocRequestDTO docRequestDTO) {
        orderService.addRKO(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PostMapping("/api/v1/orders/pko")
    public ResponseEntity<Response<String>> setCreditOrder(@RequestBody DocRequestDTO docRequestDTO) {
        orderService.addPKO(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PutMapping("/api/v1/orders/rko")
    public ResponseEntity<Response<String>> updateWithdrawOrder(@RequestBody DocRequestDTO docRequestDTO) {
        orderService.updateRKO(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PutMapping("/api/v1/orders/pko")
    public ResponseEntity<Response<String>> updateCreditOrder(@RequestBody DocRequestDTO docRequestDTO) {
        orderService.updatePKO(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @DeleteMapping("/api/v1/orders")
    public ResponseEntity<Response<String>> deleteOrder(@RequestBody DocRequestDTO docRequestDTO) {
        orderService.deleteOrderDoc(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }
}
