package com.example.sklad.services;

import com.example.sklad.exceptions.BadRequestException;
import com.example.sklad.model.entities.User;
import com.example.sklad.repositories.UserRepository;
import com.example.sklad.utils.Constants;
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

    public User getById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_USER_MESSAGE));
    }

}
