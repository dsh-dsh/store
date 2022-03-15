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
    public ResponseEntity<Response<String>> setCheckDoc(@RequestBody ItemDocRequestDTO itemDocRequestDTO) {
        documentService.addCheckDoc(itemDocRequestDTO.getItemDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PostMapping("/api/v1/docs/receipt")
    public ResponseEntity<Response<String>> setReceiptDoc(@RequestBody ItemDocRequestDTO itemDocRequestDTO) {
        documentService.addReceiptDoc(itemDocRequestDTO.getItemDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PostMapping("/api/v1/docs/posting")
    public ResponseEntity<Response<String>> setPostingDoc(@RequestBody ItemDocRequestDTO itemDocRequestDTO) {
        documentService.addPostingDoc(itemDocRequestDTO.getItemDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PostMapping("/api/v1/docs/request")
    public ResponseEntity<Response<String>> setRequestDoc(@RequestBody ItemDocRequestDTO itemDocRequestDTO) {
        documentService.addRequestDoc(itemDocRequestDTO.getItemDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PutMapping("/api/v1/docs/check")
    public ResponseEntity<Response<String>> updateCheckDoc(@RequestBody ItemDocRequestDTO itemDocRequestDTO) {
        documentService.updateCheckDoc(itemDocRequestDTO.getItemDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PutMapping("/api/v1/docs/receipt")
    public ResponseEntity<Response<String>> updateReceiptDoc(@RequestBody ItemDocRequestDTO itemDocRequestDTO) {
        documentService.updateReceiptDoc(itemDocRequestDTO.getItemDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PutMapping("/api/v1/docs/posting")
    public ResponseEntity<Response<String>> updatePostingDoc(@RequestBody ItemDocRequestDTO itemDocRequestDTO) {
        documentService.updatePostingDoc(itemDocRequestDTO.getItemDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PutMapping("/api/v1/docs/request")
    public ResponseEntity<Response<String>> updateRequestDoc(@RequestBody ItemDocRequestDTO itemDocRequestDTO) {
        documentService.updateRequestDoc(itemDocRequestDTO.getItemDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

}
