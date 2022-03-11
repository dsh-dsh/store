package com.example.sklad.services;

import com.example.sklad.exceptions.BadRequestException;
import com.example.sklad.model.entities.User;
import com.example.sklad.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(BadRequestException::new);
    }

    public User getById(long id) {
        return userRepository.findById(id)
                .orElseThrow(BadRequestException::new);
    }

}
