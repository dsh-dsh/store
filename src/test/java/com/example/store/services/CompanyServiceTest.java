package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.mappers.CompanyMapper;
import com.example.store.model.dto.CompanyDTO;
import com.example.store.model.dto.ItemDTOForTree;
import com.example.store.model.entities.Company;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class CompanyServiceTest {

    @Autowired
    private CompanyService companyService;
    @Autowired
    private CompanyMapper companyMapper;

    public static final String OUR_INN = "230902612219";
    public static final int MISSING_ID = 155;

    @Test
    void getByIdTest() {
        Company company = companyService.getById(1);
        assertEquals(OUR_INN, company.getInn());
    }

    @Test
    void getByIdThrowingExceptionTest() {
        assertThrows(BadRequestException.class, () -> companyService.getById(MISSING_ID));
    }

    @Test
    void getByNameTest() {
        Company company = companyService.getByName("ООО \"Защита\"");
        assertEquals("230000000001", company.getInn());
    }

    @Test
    void getByNameThrowingExceptionTest() {
        assertThrows(BadRequestException.class, () -> companyService.getByName("wrong company name"));
    }

    @Test
    void getByInnTest() {
        Company company = companyService.getByInn(OUR_INN);
        assertEquals("ИП Шипилов М.В.", company.getName());
    }

    @Test
    void getByInnThrowingExceptionTest() {
        assertThrows(BadRequestException.class, () -> companyService.getByInn("23090261"));
    }

    @Sql(value = "/sql/company/addCompany.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/company/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void findByCodeTrueTest() {
        Optional<Company> company = companyService.findByCode(123);
        assertTrue(company.isPresent());
        assertEquals("Company name", company.get().getName());
    }

    @Sql(value = "/sql/company/addCompany.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/company/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void findByCodeFalseTest() {
        Optional<Company> company = companyService.findByCode(321);
        assertFalse(company.isPresent());
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

    @Sql(value = "/sql/company/addCompaniesForTree.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/company/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void getCompanyDTOTreeTest() {
        List<ItemDTOForTree> list = companyService.getCompanyDTOTree();
        assertEquals(5, list.size());
        assertEquals(0, list.get(2).getChildren().size());
        assertEquals(1, list.get(3).getChildren().size());
    }

    @Test
    @Transactional
    void getCompanyByIdTest() {
        CompanyDTO dto = companyService.getCompanyById(1);
        assertEquals(OUR_INN, dto.getInn());
        assertTrue(dto.isMine());
    }

    @Sql(value = "/sql/company/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setCompanyTest() {
        CompanyDTO dto = getCompanyDTO(0);
        companyService.setCompany(dto);
        Company company = companyService.getByCode(123);
        assertEquals("new company name", company.getName());
        assertEquals("316316241412", company.getInn());
    }

    @Sql(value = "/sql/company/addCompany.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/company/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void updateCompanyTest() {
        CompanyDTO dto = getCompanyDTO(4);
        companyService.updateCompany(dto);
        Company company = companyService.getById(4);
        assertEquals("new company name", company.getName());
        assertEquals("316316241412", company.getInn());
    }

    @NotNull
    private CompanyDTO getCompanyDTO(int id) {
        CompanyDTO dto = new CompanyDTO();
        dto.setId(id);
        dto.setName("new company name");
        dto.setInn("316316241412");
        dto.setCode(123);
        return dto;
    }
}