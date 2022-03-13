package com.example.sklad.controllers;

import com.example.sklad.model.dto.requests.ItemDocListRequestDTO;
import com.example.sklad.model.dto.requests.OrderRequestDTO;
import com.example.sklad.model.responses.Response;
import com.example.sklad.services.DocsFrom1cService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Document1cController {

    @Autowired
    private DocsFrom1cService docsFrom1cService;

    @PostMapping("/docs/checks")
    public ResponseEntity<Response<String>> addCheckDocsFrom1C(@RequestBody ItemDocListRequestDTO itemDocListRequestDTO) {
        System.out.println(itemDocListRequestDTO);
        docsFrom1cService.addCheckDocsFrom1C(itemDocListRequestDTO);

        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PostMapping("/docs/orders")
    public ResponseEntity<Response<String>> addOrderDocsFrom1C(@RequestBody OrderRequestDTO orderRequestDTO) {
        System.out.println(orderRequestDTO);
        docsFrom1cService.addOrderDocsFrom1C(orderRequestDTO);

        return ResponseEntity.ok(new Response<>("ok"));
    }

}
