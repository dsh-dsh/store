package com.example.sklad.controllers;

import com.example.sklad.model.dto.requests.ItemDocRequestDTO;
import com.example.sklad.model.responses.Response;
import com.example.sklad.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/api/v1/docs")
    public ResponseEntity<Response<String>> setDocument(@RequestBody ItemDocRequestDTO itemDocRequestDTO) {
        documentService.addCheckDoc(itemDocRequestDTO.getItemDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

}
