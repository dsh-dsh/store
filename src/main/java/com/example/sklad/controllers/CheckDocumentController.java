package com.example.sklad.controllers;

import com.example.sklad.model.dto.requests.CheckRequestDTO;
import com.example.sklad.model.responses.Response;
import com.example.sklad.services.CheckDocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckDocumentController {

    @Autowired
    private CheckDocService checkDocService;

    @PostMapping("/check/docs")
    public ResponseEntity<Response<String>> addCheckDocsFrom1C(@RequestBody CheckRequestDTO checkRequestDTO) {
        System.out.println(checkRequestDTO);
        checkDocService.addCheckDocsFrom1C(checkRequestDTO);

        return ResponseEntity.ok(new Response<>("ok"));
    }

}
