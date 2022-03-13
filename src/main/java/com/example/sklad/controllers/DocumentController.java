package com.example.sklad.controllers;

import com.example.sklad.model.dto.requests.ItemDocRequestDTO;
import com.example.sklad.model.responses.Response;
import com.example.sklad.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/api/v1/docs/check")
    public ResponseEntity<Response<String>> setCheckDocument(@RequestBody ItemDocRequestDTO itemDocRequestDTO) {
        documentService.addCheckDoc(itemDocRequestDTO.getItemDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PostMapping("/api/v1/docs/receipt")
    public ResponseEntity<Response<String>> setReceiptDocument(@RequestBody ItemDocRequestDTO itemDocRequestDTO) {
        documentService.addReceiptDoc(itemDocRequestDTO.getItemDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PostMapping("/api/v1/docs/posting")
    public ResponseEntity<Response<String>> setPostingDocument(@RequestBody ItemDocRequestDTO itemDocRequestDTO) {
        documentService.addPostingDoc(itemDocRequestDTO.getItemDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PostMapping("/api/v1/docs/request")
    public ResponseEntity<Response<String>> setRequestDocument(@RequestBody ItemDocRequestDTO itemDocRequestDTO) {
        documentService.addRequestDoc(itemDocRequestDTO.getItemDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PutMapping("/api/v1/docs/check")
    public ResponseEntity<Response<String>> updateCheckDocument(@RequestBody ItemDocRequestDTO itemDocRequestDTO) {
        documentService.updateCheckDoc(itemDocRequestDTO.getItemDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

}
