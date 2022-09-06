package com.example.store.controllers;

import com.example.store.model.dto.PersonDTO;
import com.example.store.model.dto.requests.AuthUserRequest;
import com.example.store.model.responses.Response;
import com.example.store.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Response<PersonDTO>> login(@RequestBody AuthUserRequest authUserRequest) {
        Response<PersonDTO> response =
                new Response<>(authService.login(authUserRequest));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/1c/login")
    public ResponseEntity<Response<String>> login1C(@RequestBody AuthUserRequest authUserRequest) {
        String token = "<" + authService.login(authUserRequest).getToken() + ">";
        Response<String> response = new Response<>(token);

        return ResponseEntity.ok(response);
    }

}
