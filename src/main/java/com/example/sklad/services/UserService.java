package com.example.sklad.services;

import com.example.sklad.exceptions.BadRequestException;
import com.example.sklad.mappers.PersonMapper;
import com.example.sklad.model.dto.PersonDTO;
import com.example.sklad.model.entities.User;
import com.example.sklad.model.enums.Role;
import com.example.sklad.repositories.UserRepository;
import com.example.sklad.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public PersonDTO getPersonById(int id) {
        User user = getById(id);
        return personMapper.mapToUserDTO(user);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(BadRequestException::new);
    }

    public User getById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_USER_MESSAGE));
    }

    public void setPerson(PersonDTO personDTO) {
        User user = personMapper.mapToUser(personDTO);
        user.setRegTime(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(personDTO.getPassword()));
        userRepository.save(user);
    }

    public void updatePerson(PersonDTO personDTO) {
        User user = getByEmail(personDTO.getEmail());
        updateUserFields(user, personDTO);
        userRepository.save(user);
    }

    private void updateUserFields(User user, PersonDTO dto) {
        if(dto.getFirstName() != null && !dto.getFirstName().equals("")) user.setFirstName(dto.getFirstName());
        if(dto.getLastName() != null && !dto.getLastName().equals("")) user.setLastName(dto.getLastName());
        if(dto.getEmail() != null && !dto.getEmail().equals("")) user.setEmail(dto.getEmail());
        if(dto.getPhone() != null && !dto.getPhone().equals("")) user.setPhone(dto.getPhone());
        if(dto.getRegDate() !=0) user.setRegTime(Instant
                .ofEpochMilli(dto.getRegDate()).atZone(ZoneId.systemDefault()).toLocalDateTime());
        if(dto.getBirthDate() !=0) user.setBirthDate(Instant
                .ofEpochMilli(dto.getBirthDate()).atZone(ZoneId.systemDefault()).toLocalDate());
        if(dto.getRole() != null && !dto.getRole().equals("")) user.setRole(Role.valueOf(dto.getRole()));
    }
}
