package com.example.store.controllers;

import com.example.store.mappers.PersonMapper;
import com.example.store.model.dto.ItemDTOForTree;
import com.example.store.model.dto.PersonDTO;
import com.example.store.model.responses.ListResponse;
import com.example.store.model.responses.Response;
import com.example.store.services.UserService;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PersonMapper personMapper;

    @GetMapping("/tree")
    public ResponseEntity<ListResponse<ItemDTOForTree>> getItemTree() {
        return ResponseEntity.ok(new ListResponse<>(userService.getUserDTOTree()));
    }

    @GetMapping
    public ResponseEntity<Response<PersonDTO>> getPerson(@RequestParam int id) {
        PersonDTO person = userService.getPersonById(id);
        return ResponseEntity.ok(new Response<>(person));
    }

    @PostMapping
    public ResponseEntity<Response<String>> setPerson(@RequestBody PersonDTO personDTO) {
        userService.setPerson(personDTO);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

    @PutMapping
    public ResponseEntity<Response<String>> updatePerson(@RequestBody PersonDTO personDTO) {
        userService.updatePerson(personDTO);
        return ResponseEntity.ok(new Response<>(Constants.OK));
    }

}
