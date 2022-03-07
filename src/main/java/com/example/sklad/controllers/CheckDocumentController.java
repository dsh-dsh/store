package com.example.sklad.controllers;

import com.example.sklad.model.requests.CheckRequest;
import com.example.sklad.model.responses.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckDocumentController {

    @PostMapping("/check/docs")
    public ResponseEntity<Response<String>> setChecks(@RequestBody CheckRequest checkRequest) {
        System.out.println(checkRequest.getString());
        System.out.println(checkRequest.getInteger());
        return ResponseEntity.ok(new Response<>("ok"));
    }

}
