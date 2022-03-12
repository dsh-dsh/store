package com.example.sklad.controllers;

import com.example.sklad.model.dto.documents.ItemDocDTO;
import com.example.sklad.model.responses.ListResponse;
import com.example.sklad.services.PostingDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Pageable;

@RestController("/api/v1/docs/posting")
public class PostingDocumentController {

    @Autowired
    private PostingDocumentService postingDocumentService;

    @GetMapping()
    public ResponseEntity<ListResponse<ItemDocDTO>> getPostingDocuments(Pageable pageable) {
        return ResponseEntity.ok(postingDocumentService.getDocuments());
    }

}
