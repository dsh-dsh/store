package com.example.store.services;

import com.example.store.controllers.TestService;
import com.example.store.model.dto.ItemDTOForTree;
import com.example.store.model.dto.PersonDTO;
import com.example.store.model.dto.UserDTO;
import com.example.store.model.entities.User;
import com.example.store.model.enums.Role;
import com.example.store.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    private final static int CUSTOMER_ID = 1;
    private final static int CASHIER_ID = 2;
    private final static int ACCOUNTANT_ID = 3;
    private final static int ADMIN_ID = 4;

    private final static String NEW_USER_EMAIL = "new_user@mail.ru";
    private final static String CASHIER_EMAIL = "cashier@mail.ru";
    private final static String ACCOUNTANT_EMAIL = "accountant@mail.ru";
    private final static String ADMIN_EMAIL = "admin@mail.ru";
    private final static String CUSTOMER_EMAIL = "customer@mail.ru";

    private final static String CUSTOMER_ROLE_STRING = "CUSTOMER";
    private final static String ADMIN_ROLE_STRING = "ADMIN";
    private final static String CASHIER_ROLE_STRING = "CASHIER";
    private final static String ACCOUNTANT_ROLE_STRING = "ACCOUNTANT";

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Smiths";
    private static final String PASSWORD = "password";
    private static final long BIRTH_DATE = 978307200000L; // 2001-01-01
    private static final String PHONE = "+7(900)0000000";
    private static final String BIRTH_DATE_STR = "2001-01-01";

    @Test
    void getUserDTOListTest() {
        List<UserDTO> users = userService.getUserDTOList(false);
        assertEquals(4, users.size());
    }

    @Test
    void getAllUserDTOListTest() {
        List<UserDTO> users = userService.getUserDTOList(true);
        assertEquals(6, users.size());
    }

    @Test
    void getUserTree() {
        List<ItemDTOForTree> list = userService.getUserDTOTree();
        assertEquals(1, list.size());
    }

    @Test
    void getCustomerPersonByIdTest() {
        PersonDTO dto = userService.getPersonById(CUSTOMER_ID);
        assertEquals(CUSTOMER_EMAIL, dto.getEmail());
        assertEquals(CUSTOMER_ROLE_STRING, dto.getRole());
    }

    @Test
    void getAdminPersonByIdTest() {
        PersonDTO dto = userService.getPersonById(ADMIN_ID);
        assertEquals(ADMIN_EMAIL, dto.getEmail());
        assertEquals(ADMIN_ROLE_STRING, dto.getRole());
    }

    @Test
    void getAdminUserByEmailTest() {
        User user = userService.getByEmail(CASHIER_EMAIL);
        assertEquals(CASHIER_ROLE_STRING, user.getRole().toString());
        assertEquals(CASHIER_ID, user.getId());
    }

    @Test
    void getAccountantByIdTest() {
        User user = userService.getById(ACCOUNTANT_ID);
        assertEquals(ACCOUNTANT_EMAIL, user.getEmail());
        assertEquals(ACCOUNTANT_ROLE_STRING, user.getRole().toString());
    }

    @Test
    void getSystemAuthorTest() {
        User user = userService.getSystemAuthor();
        assertEquals("system@user.com", user.getEmail());
    }

    @Sql(value = "/sql/users/addNewUser.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/users/deleteNewUser.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void updatePersonTest() {
        PersonDTO personDTO = getPersonDTO();
        userService.updatePerson(personDTO);
        User user = userService.getByEmail(NEW_USER_EMAIL);
        assertEquals(FIRST_NAME, user.getFirstName());
        assertEquals(LAST_NAME, user.getLastName());
    }

    @Sql(value = "/sql/users/deleteNewUser.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setPersonTest() {
        PersonDTO personDTO = getPersonDTO();
        personDTO.setPassword(PASSWORD);
        userService.setPerson(personDTO);
        Optional<User> optionalUser = userRepository.findByEmailIgnoreCase(NEW_USER_EMAIL);
        assertFalse(optionalUser.isEmpty());
        User user = optionalUser.get();
        assertEquals(FIRST_NAME, user.getFirstName());
        assertEquals(LAST_NAME, user.getLastName());
        assertEquals(0, userRepository.getParentId(user.getId()));
    }

    @Test
    void updateUserFieldsTest() {
        User user = new User();
        userService.updateUserFields(user, getPersonDTO());
        assertEquals(FIRST_NAME, user.getFirstName());
        assertEquals(LAST_NAME, user.getLastName());
        assertEquals(NEW_USER_EMAIL, user.getEmail());
        assertEquals(BIRTH_DATE_STR, user.getBirthDate().toString());
        assertEquals(PHONE, user.getPhone());
        assertEquals(Role.CUSTOMER, user.getRole());
        assertNull(user.getPassword());
    }

    @WithUserDetails(ADMIN_EMAIL)
    @Test
    void getCurrentUserTest() {
        User user = userService.getCurrentUser();
        assertEquals("Васильев", user.getLastName());
        assertEquals(Role.ADMIN, user.getRole());
    }

    private PersonDTO getPersonDTO() {
        return PersonDTO.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .email(NEW_USER_EMAIL)
                .birthDate(BIRTH_DATE)
                .phone(PHONE)
                .role(Role.CUSTOMER.toString())
                .build();
    }



}
