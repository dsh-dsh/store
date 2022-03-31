package com.example.store.controllers;

import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.dto.documents.DocToListDTO;
import com.example.store.model.dto.requests.DocRequestDTO;
import com.example.store.model.enums.DocumentType;
import com.example.store.model.responses.ListResponse;
import com.example.store.model.responses.Response;
import com.example.store.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping("/api/v1/docs/list")
    public ResponseEntity<ListResponse<DocToListDTO>> getDocuments(
            @RequestParam(required = false) DocumentType type,
            Pageable pageable) {
        return ResponseEntity.ok(documentService.getDocumentsByType(type, pageable));
    }

    @GetMapping("/api/v1/docs")
    public ResponseEntity<Response<DocDTO>> getDocumentById(@RequestParam int id) {
        DocDTO docDTO = documentService.getDocDTOById(id);
        return ResponseEntity.ok(new Response<>(docDTO));
    }

    @PostMapping("/api/v1/docs")
    public ResponseEntity<Response<String>> addDocument(@RequestBody DocRequestDTO docRequestDTO) {
        documentService.addDocument(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PutMapping("/api/v1/docs")
    public ResponseEntity<Response<String>> updateDocument(@RequestBody DocRequestDTO docRequestDTO) {
        documentService.updateDocument(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @DeleteMapping("/api/v1/docs")
    public ResponseEntity<Response<String>> softDeleteDocument(@RequestBody DocRequestDTO docRequestDTO) {
        documentService.softDeleteDocument(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

}
