package com.example.store.services;

import com.example.store.mappers.PersonMapper;
import com.example.store.model.dto.PersonDTO;
import com.example.store.model.dto.requests.AuthUserRequest;
import com.example.store.model.entities.User;
import com.example.store.repositories.UserRepository;
import com.example.store.security.JwtProvider;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PersonMapper personMapper;

    public PersonDTO login(AuthUserRequest authUserRequest) {

        User user;
        try{
            user = userService.getByEmail(authUserRequest.getLogin());
        } catch (Exception ex) {
            throw new AuthenticationCredentialsNotFoundException(
                    String.format(
                            Constants.WRONG_CREDENTIALS_MESSAGE,
                            this.getClass().getName() + " login(AuthUserRequest authUserRequest)"));
        }

        if(!passwordEncoder.matches(authUserRequest.getPassword(), user.getPassword())) {
            throw new AuthenticationCredentialsNotFoundException(String.format(
                    Constants.WRONG_CREDENTIALS_MESSAGE,
                    this.getClass().getName() + " login(AuthUserRequest authUserRequest)"));
        }
        PersonDTO personDTO = personMapper.mapToPersonDTO(user);
        personDTO.setToken(jwtProvider.generateToken(user));

        return personDTO;
    }


}
