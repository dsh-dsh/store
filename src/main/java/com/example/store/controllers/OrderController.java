package com.example.store.controllers;

import com.example.store.model.dto.documents.OrderDTO;
import com.example.store.model.dto.requests.DocRequestDTO;
import com.example.store.model.responses.Response;
import com.example.store.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<Response<OrderDTO>> getOrder(@RequestParam int id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(new Response<>(order));
    }

    @PostMapping
    public ResponseEntity<Response<String>> addOrder(@RequestBody DocRequestDTO docRequestDTO) {
        orderService.addOrder(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PutMapping
    public ResponseEntity<Response<String>> updateOrder(@RequestBody DocRequestDTO docRequestDTO) {
        orderService.updateOrder(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @DeleteMapping
    public ResponseEntity<Response<String>> deleteOrder(@RequestBody DocRequestDTO docRequestDTO) {
        orderService.deleteOrderDoc(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }
}
