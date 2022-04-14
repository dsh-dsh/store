package com.example.store.controllers;

import com.example.store.model.dto.requests.ItemDocListRequestDTO;
import com.example.store.model.responses.Response;
import com.example.store.services.DocsFrom1cService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Document1cController {

    @Autowired
    private DocsFrom1cService docsFrom1cService;

    @PostMapping("/docs/1с")
    public ResponseEntity<Response<String>> addDocsFrom1C(@RequestBody ItemDocListRequestDTO itemDocListRequestDTO) {
        docsFrom1cService.addDocsFrom1C(itemDocListRequestDTO);
        return ResponseEntity.ok(new Response<>("ok"));
    }

}
