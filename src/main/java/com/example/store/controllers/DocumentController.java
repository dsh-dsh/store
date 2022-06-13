package com.example.store.controllers;

import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.dto.documents.DocToListDTO;
import com.example.store.model.dto.requests.DocRequestDTO;
import com.example.store.model.responses.ListResponse;
import com.example.store.model.responses.Response;
import com.example.store.services.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/docs")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @GetMapping("/list")
    public ResponseEntity<ListResponse<DocToListDTO>> getDocuments(
            @RequestParam(defaultValue = "") String filter,
            Pageable pageable) {
        return ResponseEntity.ok(documentService.getDocumentsByFilter(filter, pageable));
    }

    @GetMapping
    public ResponseEntity<Response<DocDTO>> getDocumentById(@RequestParam int id) {
        DocDTO docDTO = documentService.getDocDTOById(id);
        return ResponseEntity.ok(new Response<>(docDTO));
    }

    @PostMapping
    public ResponseEntity<Response<String>> addDocument(@RequestBody DocRequestDTO docRequestDTO) {
        documentService.addDocument(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PostMapping("/hold/{id}")
    public ResponseEntity<Response<String>> holdDocument(@PathVariable int id) {
        documentService.holdDocument(id);
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PutMapping
    public ResponseEntity<Response<String>> updateDocument(@RequestBody DocRequestDTO docRequestDTO) {
        documentService.updateDocument(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @DeleteMapping
    public ResponseEntity<Response<String>> softDeleteDocument(@RequestBody DocRequestDTO docRequestDTO) {
        documentService.softDeleteDocument(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

}
