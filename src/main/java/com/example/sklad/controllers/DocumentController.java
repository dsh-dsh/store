package com.example.sklad.controllers;

import com.example.sklad.model.dto.requests.DocRequestDTO;
import com.example.sklad.model.responses.Response;
import com.example.sklad.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/api/v1/docs/check")
    public ResponseEntity<Response<String>> setCheckDoc(@RequestBody DocRequestDTO docRequestDTO) {
        documentService.addCheckDoc(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PostMapping("/api/v1/docs/receipt")
    public ResponseEntity<Response<String>> setReceiptDoc(@RequestBody DocRequestDTO docRequestDTO) {
        documentService.addReceiptDoc(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PostMapping("/api/v1/docs/posting")
    public ResponseEntity<Response<String>> setPostingDoc(@RequestBody DocRequestDTO docRequestDTO) {
        documentService.addPostingDoc(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PostMapping("/api/v1/docs/inventory")
    public ResponseEntity<Response<String>> setInventoryDoc(@RequestBody DocRequestDTO docRequestDTO) {
        documentService.addInventoryDoc(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PostMapping("/api/v1/docs/request")
    public ResponseEntity<Response<String>> setRequestDoc(@RequestBody DocRequestDTO docRequestDTO) {
        documentService.addRequestDoc(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PutMapping("/api/v1/docs/check")
    public ResponseEntity<Response<String>> updateCheckDoc(@RequestBody DocRequestDTO docRequestDTO) {
        documentService.updateCheckDoc(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PutMapping("/api/v1/docs/receipt")
    public ResponseEntity<Response<String>> updateReceiptDoc(@RequestBody DocRequestDTO docRequestDTO) {
        documentService.updateReceiptDoc(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PutMapping("/api/v1/docs/posting")
    public ResponseEntity<Response<String>> updatePostingDoc(@RequestBody DocRequestDTO docRequestDTO) {
        documentService.updatePostingDoc(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PutMapping("/api/v1/docs/request")
    public ResponseEntity<Response<String>> updateRequestDoc(@RequestBody DocRequestDTO docRequestDTO) {
        documentService.updateRequestDoc(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PutMapping("/api/v1/docs/inventory")
    public ResponseEntity<Response<String>> updateInventoryDoc(@RequestBody DocRequestDTO docRequestDTO) {
        documentService.updateInventoryDoc(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @DeleteMapping("/api/v1/docs")
    public ResponseEntity<Response<String>> deleteDocument(@RequestBody DocRequestDTO docRequestDTO) {
        documentService.deleteDocument(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

}
