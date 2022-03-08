package com.example.sklad.controllers;

import com.example.sklad.model.dto.requests.CheckRequestDTO;
import com.example.sklad.model.responses.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckDocumentController {

    @PostMapping("/check/docs")
    public ResponseEntity<Response<String>> setChecks(@RequestBody CheckRequestDTO checkRequestDTO) {
        System.out.println(checkRequestDTO.getString());
        System.out.println(checkRequestDTO.getInteger());
        return ResponseEntity.ok(new Response<>("ok"));
    }

}
