package com.example.sklad.controllers;

import com.example.sklad.mappers.PersonMapper;
import com.example.sklad.model.dto.PersonDTO;
import com.example.sklad.model.responses.Response;
import com.example.sklad.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PersonMapper personMapper;

    @GetMapping("/api/v1/users")
    public ResponseEntity<Response<PersonDTO>> getPerson(@RequestParam int id) {
        PersonDTO person = userService.getPersonById(id);
        return ResponseEntity.ok(new Response<>(person));
    }

}
