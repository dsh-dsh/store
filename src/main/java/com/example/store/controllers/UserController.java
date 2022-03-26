package com.example.store.controllers;

import com.example.store.mappers.PersonMapper;
import com.example.store.model.dto.PersonDTO;
import com.example.store.model.responses.Response;
import com.example.store.services.UserService;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/api/v1/users")
    public ResponseEntity<Response<String>> setPerson(@RequestBody PersonDTO personDTO) {
        userService.setPerson(personDTO);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PutMapping("/api/v1/users")
    public ResponseEntity<Response<String>> updatePerson(@RequestBody PersonDTO personDTO) {
        userService.updatePerson(personDTO);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

}
