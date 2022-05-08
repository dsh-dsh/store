package com.example.store.controllers;

import com.example.store.model.responses.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DefaultController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/home")
    @ResponseBody
    public ResponseEntity<Response<String[]>> home() {
        String[] strArr = new String[]{"a", "b", "c"};
        return ResponseEntity.ok(new Response<>(strArr));
    }

}
