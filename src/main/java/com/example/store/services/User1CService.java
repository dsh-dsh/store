package com.example.store.services;

import com.example.store.model.dto.User1CDTO;
import com.example.store.model.dto.requests.UserList1CRequestDTO;
import com.example.store.model.entities.User;
import com.example.store.model.enums.Role;
import com.example.store.utils.Util;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class User1CService extends UserService {

    // todo add tests

    public void setUsersFrom1C(UserList1CRequestDTO userList1CRequestDTO) {
        List<User1CDTO> dtoList = userList1CRequestDTO.getUser1CDTOList();
        dtoList.sort(Comparator.comparing(User1CDTO::getCode));
        addRootUsers(dtoList);
        setUserRecursive(new ArrayList<>(dtoList));
    }

    protected void addRootUsers(List<User1CDTO> dtoList) {
        dtoList.stream().filter(dto -> dto.getParentId() == 0).forEach(this::setUser);
        setNullParentIdFieldsToIntNullInDB();
    }

    protected void setNullParentIdFieldsToIntNullInDB() {
        List<User> items = userRepository.findByParent(null);
        items.forEach(item -> userRepository.setParentIdNotNull(item.getId()));
    }

    private void setUserRecursive(List<User1CDTO> dtoList) {
        if(!dtoList.isEmpty()) {
            Iterator<User1CDTO> iterator = dtoList.iterator();
            boolean interrupt = true;
            while (iterator.hasNext()) {
                User1CDTO dto = iterator.next();
                if (userRepository.existsByCode(dto.getParentId())) {
                    setUser(dto);
                    iterator.remove();
                    interrupt = false;
                }
            }
            if(interrupt) return; // where is not any parent node, so infinity loop
            setUserRecursive(dtoList);
        }
    }

    public void setUser(User1CDTO dto) {
        Optional<User> userOptional = findByCode(dto.getCode());
        if(userOptional.isPresent()) {
            updatePerson(dto);
        } else {
            setPerson(dto);
        }
    }

    // todo update tests
    public void updatePerson(User1CDTO user1CDTO) {
        User user = getByCode(user1CDTO.getCode());
        updateUserFields(user, user1CDTO);
        userRepository.save(user);
    }

    // todo update tests
    public void updateUserFields(User user, User1CDTO dto) {
        if(dto.getName() != null && !dto.getName().isEmpty()) user.setLastName(dto.getName());
        user.setFirstName("");
        if(dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            user.setEmail(dto.getEmail());
        } else {
            user.setEmail(dto.getCode() + "@email.com");
        }
        if(!dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if(dto.getPhone() != null && !dto.getPhone().isEmpty()) user.setPhone(dto.getPhone());
        user.setBirthDate(Util.getLocalDate(dto.getBirthDate()));
    }

    // todo update tests
    public void setPerson(User1CDTO user1CDTO) {
        User user = personMapper.mapToUser(user1CDTO);
        user.setFirstName("");
        user.setRegTime(LocalDateTime.now());
        if(user1CDTO.getEmail() != null && !user1CDTO.getEmail().isEmpty()) {
            user.setEmail(user1CDTO.getEmail());
        } else {
            user.setEmail(user1CDTO.getCode() + "@email.com");
        }
        if(user1CDTO.getPassword() != null && !user1CDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user1CDTO.getPassword()));
        }
        user.setRole(Role.NONE);
        userRepository.save(user);
    }

    private Optional<User> findByCode(int code) {
        return userRepository.findByCode(code);
    }

}
