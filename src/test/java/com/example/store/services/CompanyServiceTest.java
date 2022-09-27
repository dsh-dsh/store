package com.example.store.services;

import com.example.store.mappers.CompanyMapper;
import com.example.store.model.dto.CompanyDTO;
import com.example.store.model.entities.Company;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class CompanyServiceTest {

    public static final long OUR_INN = 230902612219L;
    @Autowired
    private CompanyService companyService;
    @Autowired
    private CompanyMapper companyMapper;

    @Test
    void getByIdTest() {
        Company company = companyService.getById(1);
        assertEquals(OUR_INN, company.getInn());
    }

    @Test
    void getByNameTest() {
        Company company = companyService.getByName("ООО \"Защита\"");
        assertEquals(230000000001L, company.getInn());
    }

    @Test
    void getCompanyDTOListTest() {
        List<CompanyDTO> list = companyService.getCompanyDTOList();
        assertEquals(3, list.size());
    }

    @Test
    @Transactional
    void getOurCompanyTest() {
        Company company = companyService.getOurCompany();
        assertEquals(OUR_INN, company.getInn());
    }

    @Test
    @Transactional
    void mapToDTOTest() {
        Company company = companyService.getOurCompany();
        CompanyDTO dto = companyMapper.mapToDTO(company);
        assertTrue(dto.isMine());
    }
}