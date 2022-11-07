package com.example.store.controllers;

import com.example.store.ItemTestService;
import com.example.store.model.dto.Company1CDTO;
import com.example.store.model.dto.Item1CDTO;
import com.example.store.model.dto.PriceDTO;
import com.example.store.model.dto.User1CDTO;
import com.example.store.model.dto.requests.CompanyList1CRequestDTO;
import com.example.store.model.dto.requests.ItemList1CRequestDTO;
import com.example.store.model.dto.requests.UserList1CRequestDTO;
import com.example.store.model.entities.Company;
import com.example.store.model.entities.Item;
import com.example.store.model.entities.User;
import com.example.store.model.enums.PriceType;
import com.example.store.model.enums.Unit;
import com.example.store.model.enums.Workshop;
import com.example.store.repositories.CompanyRepository;
import com.example.store.repositories.ItemRepository;
import com.example.store.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class Item1CControllerTest extends TestService {

    private static final String URL_ITEMS = "/items";
    private static final String URL_USERS = "/users";
    private static final String URL_COMPANIES = "/companies";

    @Autowired
    private ItemTestService itemTestService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CompanyRepository companyRepository;

    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    @Sql(value = "/sql/items/addNewItem.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void setItemsFrom1CTest() throws Exception {

        ItemList1CRequestDTO itemList1CRequestDTO = new ItemList1CRequestDTO();
        itemList1CRequestDTO.setItem1CDTOList(getItemDTOList());

        this.mockMvc.perform(
                        post(URL_ITEMS)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemList1CRequestDTO)))
                .andDo(print())
                .andExpect(status().isOk());

        List<Item> items = itemRepository.findAll();
        assertEquals(19 , items.size());
        Item item = itemRepository.getByNumber(17);
        assertEquals(0, itemRepository.findByParent(null).size());
    }

    @Sql(value = "/sql/items/deleteNewItem.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setItemsFrom1CUnauthorizedTest() throws Exception {

        ItemList1CRequestDTO itemList1CRequestDTO = new ItemList1CRequestDTO();
        itemList1CRequestDTO.setItem1CDTOList(getItemDTOList());

        this.mockMvc.perform(
                        post(URL_ITEMS)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemList1CRequestDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/users/deleteNewUsers.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void setUserFrom1CTest() throws Exception {

        UserList1CRequestDTO userList1CRequestDTO = new UserList1CRequestDTO();
        userList1CRequestDTO.setUser1CDTOList(getUserDTOList());

        this.mockMvc.perform(
                        post(URL_USERS)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userList1CRequestDTO)))
                .andDo(print())
                .andExpect(status().isOk());
        List<User> users = userRepository.findAll();
        assertEquals(16, users.size());
    }

    @Sql(value = "/sql/users/deleteNewUsers.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setUserFrom1CUnauthorizedTest() throws Exception {

        UserList1CRequestDTO userList1CRequestDTO = new UserList1CRequestDTO();
        userList1CRequestDTO.setUser1CDTOList(getUserDTOList());

        this.mockMvc.perform(
                        post(URL_USERS)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userList1CRequestDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Sql(value = "/sql/company/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @WithUserDetails(TestService.EXISTING_EMAIL)
    void setCompaniesFrom1CTest() throws Exception {

        CompanyList1CRequestDTO companyList1CRequestDTO = new CompanyList1CRequestDTO();
        companyList1CRequestDTO.setCompany1CDTOList(getCompaniesDTOList());

        this.mockMvc.perform(
                        post(URL_COMPANIES)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(companyList1CRequestDTO)))
                .andDo(print())
                .andExpect(status().isOk());
        List<Company> companies = companyRepository.findAll();
        assertEquals(13, companies.size());
    }

    @Sql(value = "/sql/company/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setCompaniesFrom1CUnauthorizedTest() throws Exception {

        CompanyList1CRequestDTO companyList1CRequestDTO = new CompanyList1CRequestDTO();
        companyList1CRequestDTO.setCompany1CDTOList(getCompaniesDTOList());

        this.mockMvc.perform(
                        post(URL_COMPANIES)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(companyList1CRequestDTO)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    private List<Company1CDTO> getCompaniesDTOList() {
        List<Company1CDTO> list = new ArrayList<>();
        list.add(getCompany1CDTO(111, 0));
        list.add(getCompany1CDTO(222, 0));
        list.add(getCompany1CDTO(333, 222));
        list.add(getCompany1CDTO(444, 0));
        list.add(getCompany1CDTO(555, 222));
        list.add(getCompany1CDTO(666, 111));
        list.add(getCompany1CDTO(777, 222));
        list.add(getCompany1CDTO(888, 444));
        list.add(getCompany1CDTO(999, 1000));
        list.add(getCompany1CDTO(1000, 0));
        return list;
    }


    @NotNull
    private Company1CDTO getCompany1CDTO(int code, int parentId) {
        Company1CDTO dto = new Company1CDTO();
        dto.setName("new company name");
        dto.setInn("316316241412");
        dto.setCode(code);
        dto.setParentId(parentId);
        return dto;
    }

    private List<User1CDTO> getUserDTOList() {
        List<User1CDTO> list = new ArrayList<>();
        list.add(getUserDTO(10, 1, "user10", "10@mail.ru", "", 1234567890, false, ""));
        list.add(getUserDTO(11, 1, "user11", "11@mail.ru", "", 1234567890, false, ""));
        list.add(getUserDTO(15, 13, "user15", "15@mail.ru", "", 1234567890, false, ""));
        list.add(getUserDTO(17, 16, "user17", "17@mail.ru", "", 1234567890, false, ""));
        list.add(getUserDTO(18, 16, "user18", "18@mail.ru", "", 1234567890, false, ""));
        list.add(getUserDTO(19, 16, "user19", "19@mail.ru", "", 1234567890, false, ""));
        list.add(getUserDTO(12, 1, "user12", "12@mail.ru", "", 1234567890, false, ""));
        list.add(getUserDTO(16, 13, "dirUser16", "16@mail.ru", "", 1234567890, true, null));
        list.add(getUserDTO(13, 0, "rootUser2", "13@mail.ru", "", 1234567890, true, ""));
        list.add(getUserDTO(14, 13, "user14", "14@mail.ru", "", 1234567890, false, "12345678"));
        return list;
    }

    private User1CDTO getUserDTO(int code, int parentId, String name,
                                 String email, String phone, long birthDate, boolean isNode, String password) {
        User1CDTO dto = new User1CDTO();
        dto.setName(name);
        dto.setCode(code);
        dto.setEmail(email);
        dto.setParentId(parentId);
        dto.setPhone(phone);
        dto.setBirthDate(birthDate);
        dto.setNode(isNode);
        dto.setPassword(password);
        return dto;
    }

    private List<Item1CDTO> getItemDTOList() {
        List<Item1CDTO> list = new ArrayList<>();
        list.add(getItemDTO(11, 2, "Бар", List.of()));
        list.add(getItemDTO(12, 11, "Ингредиент 1", List.of()));
        list.add(getItemDTO(13, 16, "Ингредиент 2", List.of()));
        list.add(getItemDTO(14, 1, "Блюдо 1",
                getPrices(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        100.00f, 120.00f)));
        list.add(getItemDTO(444, 1, "Блюдо 10",
                getPrices(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        100.00f, 120.00f)));
        list.add(getItemDTO(3611, 1, "Cуп лапша (1)",
                getPrices(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                        180.00f, 220.00f)));
        list.add(getItemDTO(15, 11, "Ингредиент 1", List.of()));
        list.add(getItemDTO(16, 11, "Ингредиент 2", List.of()));
        list.add(getItemDTO(18, 17, "Рецепт 1", List.of()));
        list.add(getItemDTO(17, 0, "Рецепты", List.of()));
        list.add(getItemDTO(19, 17, "Рецепт 2", List.of()));
        return list;
    }

    private Item1CDTO getItemDTO(int number, int parentNumber, String name, List<PriceDTO> prices) {
        Item1CDTO dto = new Item1CDTO();
        dto.setName(name);
        dto.setPrintName(name);
        dto.setRegTime(Instant.now().toEpochMilli());
        dto.setUnit(getUnitDTO(Unit.KG));
        dto.setWorkshop(getWorkshopDTO(Workshop.BAR));
        dto.setPrices(prices);
        dto.setNumber(number);
        dto.setParentNumber(parentNumber);
        return dto;
    }

    private List<PriceDTO> getPrices(long date, float retailValue, float deliveryValue){
        PriceDTO retailPrice = PriceDTO.builder()
                .date(date)
                .type(PriceType.RETAIL.toString())
                .value(retailValue)
                .build();
        PriceDTO deliveryPrice = PriceDTO.builder()
                .date(date)
                .type(PriceType.DELIVERY.toString())
                .value(deliveryValue)
                .build();
        return List.of(retailPrice, deliveryPrice);
    }

}
