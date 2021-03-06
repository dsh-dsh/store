package com.example.store.services;

import com.example.store.components.TreeBuilder;
import com.example.store.exceptions.BadRequestException;
import com.example.store.mappers.PersonMapper;
import com.example.store.model.dto.ItemDTOForTree;
import com.example.store.model.dto.PersonDTO;
import com.example.store.model.dto.UserDTO;
import com.example.store.model.entities.User;
import com.example.store.model.enums.Role;
import com.example.store.repositories.UserRepository;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TreeBuilder<User> treeBuilder;

    public PersonDTO getPersonById(int id) {
        User user = getById(id);
        return personMapper.mapToPersonDTO(user);
    }

    public User getByName(String name) {
        return userRepository.findByLastNameStartingWithIgnoreCase(name)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_USER_MESSAGE + " - " + name));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_USER_MESSAGE + " - " + email));
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

    public void updateUserFields(User user, PersonDTO dto) {
        if(dto.getFirstName() != null && !dto.getFirstName().equals("")) user.setFirstName(dto.getFirstName());
        if(dto.getLastName() != null && !dto.getLastName().equals("")) user.setLastName(dto.getLastName());
        if(dto.getEmail() != null && !dto.getEmail().equals("")) user.setEmail(dto.getEmail());
        if(dto.getPhone() != null && !dto.getPhone().equals("")) user.setPhone(dto.getPhone());
        if(dto.getBirthDate() != 0) user.setBirthDate(Instant
                .ofEpochMilli(dto.getBirthDate()).atZone(ZoneId.systemDefault()).toLocalDate());
        if(dto.getRole() != null && !dto.getRole().equals("")) user.setRole(Role.valueOf(dto.getRole()));
    }

    public List<PersonDTO> getPersonDTOList() {
        return userRepository.findAll().stream()
                .map(personMapper::mapToPersonDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getUserDTOList() {
        return userRepository.findAll().stream()
                .map(personMapper::mapToUserDTO)
                .collect(Collectors.toList());
    }

    public User getSystemAuthor() {
        String systemUserEmail = Constants.SYSTEM_USER_EMAIL;
        return userRepository.findByEmailIgnoreCase(systemUserEmail)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_USER_MESSAGE));
    }

    public List<ItemDTOForTree> getUserDTOTree() {
        List<User> users = userRepository.findAll(Sort.by("id"));
        return treeBuilder.getItemTree(users);
    }
}
