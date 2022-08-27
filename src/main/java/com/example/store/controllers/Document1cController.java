package com.example.store.controllers;

import com.example.store.model.dto.requests.ItemDocListRequestDTO;
import com.example.store.model.responses.Response;
import com.example.store.services.DocCrudService;
import com.example.store.services.SchedulerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/1с")
public class Document1cController {

    @Autowired
    private DocCrudService docCrudService;
    @Autowired
    private SchedulerService schedulerService;

    @GetMapping("/check")
    public ResponseEntity<Response<String>> checkUnHolden1CDocs() {
        String firstDate = docCrudService.checkUnHoldenChecks();
        return ResponseEntity.ok(new Response<>(firstDate));
    }

    @PostMapping("/docs")
    public ResponseEntity<Response<String>> addDocsFrom1C(
            @RequestBody ItemDocListRequestDTO itemDocListRequestDTO) {
        docCrudService.addDocsFrom1C(itemDocListRequestDTO);
        return ResponseEntity.ok(new Response<>("ok"));
    }

    @PostMapping("/hold")
    public ResponseEntity<Response<String>> hold1CDocuments() {
        schedulerService.holdChecksForADay();
        return ResponseEntity.ok(new Response<>("ok"));
    }

}
