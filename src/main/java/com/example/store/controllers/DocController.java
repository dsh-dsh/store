package com.example.store.controllers;

import com.example.store.model.dto.documents.DocDTO;
import com.example.store.model.dto.documents.DocToListDTO;
import com.example.store.model.dto.requests.DocRequestDTO;
import com.example.store.model.responses.ListResponse;
import com.example.store.model.responses.Response;
import com.example.store.services.DocCrudService;
import com.example.store.services.HoldDocsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/docs")
public class DocController {

    @Autowired
    private DocCrudService docCrudService;
    @Autowired
    private HoldDocsService holdDocsService;

    @GetMapping("/list")
    public ResponseEntity<ListResponse<DocToListDTO>> getDocuments(
            @RequestParam(defaultValue = "") String filter,
            @RequestParam(defaultValue = "0") long start,
            @RequestParam(defaultValue = "0") long end) {
        return ResponseEntity.ok(docCrudService.getDocumentsByFilter(filter, start, end));
    }

    @GetMapping
    public ResponseEntity<Response<DocDTO>> getDocumentById(@RequestParam int id) {
        DocDTO docDTO = docCrudService.getDocDTOById(id);
        return ResponseEntity.ok(new Response<>(docDTO));
    }

    @PostMapping
    public ResponseEntity<Response<String>> addDocument(@RequestBody DocRequestDTO docRequestDTO) {
        docCrudService.addDocument(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PutMapping
    public ResponseEntity<Response<String>> updateDocument(@RequestBody DocRequestDTO docRequestDTO) {
        docCrudService.updateDocument(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @DeleteMapping
    public ResponseEntity<Response<String>> softDeleteDocument(@RequestBody DocRequestDTO docRequestDTO) {
        docCrudService.softDeleteDocument(docRequestDTO.getDocDTO());
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PostMapping("/hold/{id}")
    public ResponseEntity<Response<String>> holdDocument(@PathVariable int id) {
        holdDocsService.holdDocument(id);
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PostMapping("/hold/serial/{id}")
    public ResponseEntity<Response<String>> serialHoldDocument(@PathVariable int id) {
        holdDocsService.serialHoldDocument(id);
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @GetMapping("/controller/advice/test")
    public ResponseEntity<Response<DocDTO>> getDocumentForControllerAdviceTest(@RequestParam int id) {
        DocDTO docDTO = docCrudService.getDocDTOForControllerAdviceTest(id);
        return ResponseEntity.ok(new Response<>(docDTO));
    }
}
