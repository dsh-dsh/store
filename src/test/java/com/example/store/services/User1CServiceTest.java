package com.example.store.services;

import com.example.store.model.dto.User1CDTO;
import com.example.store.model.entities.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class User1CServiceTest {

    @Autowired
    private User1CService user1CService;
    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Sql(value = "/sql/users/addNew1CUsers.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/users/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addRootUsersTest() {
        List<User1CDTO> list = new ArrayList<>();
        list.add(getUser1CDTO(111, 0));
        list.add(getUser1CDTO(222, 0));
        list.add(getUser1CDTO(333, 0));
        list.add(getUser1CDTO(444, 321));
        list.add(getUser1CDTO(555, 321));
        User1CService user1CServiceSpy = spy(user1CService);
        user1CServiceSpy.addRootUsers(list);
        verify(user1CServiceSpy, times(3)).setUser(any());
        verify(user1CServiceSpy, times(1)).setNullParentIdFieldsToIntNullInDB();
    }

    @Sql(value = "/sql/users/addNew1CUsers.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/users/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setUserRecursiveTest() {
        List<User1CDTO> list = new ArrayList<>();
        list.add(getUser1CDTO(111, 0));
        list.add(getUser1CDTO(222, 0));
        list.add(getUser1CDTO(333, 0));
        list.add(getUser1CDTO(444, 321));
        list.add(getUser1CDTO(555, 321));
        User1CService user1CServiceSpy = spy(user1CService);
        user1CServiceSpy.setUserRecursive(list);
        verify(user1CServiceSpy, times(2)).setUser(any());
    }

    @Sql(value = "/sql/users/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setUserCallSetTest() {
        User1CService user1CServiceSpy = spy(user1CService);
        User1CDTO dto = getUser1CDTO(123, 1);
        user1CServiceSpy.setUser(dto);
        verify(user1CServiceSpy, times(1)).setPerson(dto);
    }

    @Sql(value = "/sql/users/addNew1CUsers.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/users/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setUserCallUpdateTest() {
        User1CService user1CServiceSpy = spy(user1CService);
        User1CDTO dto = getUser1CDTO(123, 1);
        user1CServiceSpy.setUser(dto);
        verify(user1CServiceSpy, times(1)).updatePerson(dto);
    }

    @Sql(value = "/sql/users/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setPersonTest() {
        User1CDTO dto = getUser1CDTO(123, 1);
        user1CService.setPerson(dto);
        User user = user1CService.getByCode(123);
        assertEquals("User Name", user.getLastName());
        assertTrue(passwordEncoder.matches(dto.getPassword(), user.getPassword()));
    }

    @Sql(value = "/sql/users/addNew1CUsers.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/users/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void updatePersonTest() {
        User1CDTO dto = getUser1CDTO(123, 321);
        user1CService.updatePerson(dto);
        User user = user1CService.getByCode(123);
        assertEquals("User Name", user.getLastName());
        assertTrue(passwordEncoder.matches(dto.getPassword(), user.getPassword()));
        assertEquals(321, user.getParent().getCode());

    }

    @NotNull
    private User1CDTO getUser1CDTO(int code, int parentCode) {
        User1CDTO dto = new User1CDTO();
        dto.setName("User Name");
        dto.setCode(code);
        dto.setParentId(parentCode);
        dto.setPassword("12345678");
        return dto;
    }
}
