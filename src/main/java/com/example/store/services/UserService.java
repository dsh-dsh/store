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
import com.example.store.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected PersonMapper personMapper;
    @Autowired
    protected PasswordEncoder passwordEncoder;
    @Autowired
    private TreeBuilder<User> treeBuilder;

    public PersonDTO getPersonById(int id) {
        User user = getById(id);
        return personMapper.mapToPersonDTO(user);
    }

    public User getByCode(int code) {
        return userRepository.findByCode(code)
                .orElseThrow(() -> new BadRequestException(
                        String.format(Constants.NO_SUCH_USER_MESSAGE, code),
                        this.getClass().getName() + " - getByCode(int code)"));
    }

    public User getByName(String name) {
        return userRepository.findByLastNameStartingWithIgnoreCase(name)
                .orElseThrow(() -> new BadRequestException(
                        String.format(Constants.NO_SUCH_USER_MESSAGE, name),
                        this.getClass().getName() + " - getByName(String name)"));
    }

    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadRequestException(
                        String.format(Constants.NO_SUCH_USER_MESSAGE, email),
                        this.getClass().getName() + " - getByEmail(String email)"));
    }

    public User getById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(
                        Constants.NO_SUCH_USER_MESSAGE,
                        this.getClass().getName() + " - getById(int id)"));
    }

    public void setPerson(PersonDTO personDTO) {
        User user = personMapper.mapToUser(personDTO);
        user.setRegTime(LocalDateTime.now());
        userRepository.save(user);
        if(user.getParent() == null) {
            userRepository.setParentIdNotNull(user.getId());
        }
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
        if(dto.getBirthDate() != 0) user.setBirthDate(Util.getLocalDate(dto.getBirthDate()));
        if(dto.getRole() != null && !dto.getRole().equals("")) user.setRole(Role.valueOf(dto.getRole()));
    }

    public List<UserDTO> getUserDTOList() {
        return userRepository.findByIsNodeAndRoleNotLike(false, Role.SYSTEM, Sort.by("lastName")).stream()
                .map(personMapper::mapToUserDTO)
                .collect(Collectors.toList());
    }

    public User getSystemAuthor() {
        String systemUserEmail = Constants.SYSTEM_USER_EMAIL;
        return userRepository.findByEmailIgnoreCase(systemUserEmail)
                .orElseThrow(() -> new BadRequestException(
                        Constants.NO_SUCH_USER_MESSAGE,
                        this.getClass().getName() + " - getSystemAuthor()"));
    }

    public List<ItemDTOForTree> getUserDTOTree() {
        List<User> users = userRepository
                .findByRoleNotLike(Role.SYSTEM, Sort.by("id"));
        return treeBuilder.getItemTree(users);
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmailIgnoreCase(auth.getName())
                .orElseThrow(() -> new BadRequestException(
                        Constants.NO_SUCH_USER_MESSAGE,
                        this.getClass().getName() + " - getCurrentUser()"));
    }
}
