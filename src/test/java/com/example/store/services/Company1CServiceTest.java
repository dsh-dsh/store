package com.example.store.services;

import com.example.store.mappers.CompanyMapper;
import com.example.store.model.dto.Company1CDTO;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class Company1CServiceTest {

    @Autowired
    private Company1CService company1CService;
    @Autowired
    private CompanyMapper companyMapper;

    public static final String OUR_INN = "230902612219";
    public static final int MISSING_ID = 155;

    @Sql(value = "/sql/company/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void addRootUsersTest() {
        List<Company1CDTO> list = List.of(
            getCompany1CDTO(111, 0),
            getCompany1CDTO(222, 0),
            getCompany1CDTO(333, 666),
            getCompany1CDTO(444, 0),
            getCompany1CDTO(555, 777)
        );
        Company1CService company1CServiceSpy = spy(company1CService);
        company1CServiceSpy.addRootUsers(list);
        verify(company1CServiceSpy, times(3)).setCompanyFrom1C(any());
    }

    @Sql(value = "/sql/company/addCompaniesFor1C.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/company/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setCompanyRecursiveTest() {
        List<Company1CDTO> list = new ArrayList<>();
        list.add(getCompany1CDTO(111, 0));
        list.add(getCompany1CDTO(222, 0));
        list.add(getCompany1CDTO(333, 321));
        list.add(getCompany1CDTO(444, 0));
        list.add(getCompany1CDTO(555, 321));
        Company1CService company1CServiceSpy = spy(company1CService);
        company1CServiceSpy.setCompanyRecursive(list);
        verify(company1CServiceSpy, times(2)).setCompanyFrom1C(any());
    }

    @Sql(value = "/sql/company/addCompaniesFor1C.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/company/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setCompanyFrom1CCallSetTest() {
        Company1CService company1CServiceSpy = spy(company1CService);
        Company1CDTO dto = getCompany1CDTO(222);
        company1CServiceSpy.setCompanyFrom1C(dto);
        verify(company1CServiceSpy, times(1)).setCompany(dto);
    }

    @Sql(value = "/sql/company/addCompaniesFor1C.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/company/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setCompanyFrom1CCallUpdateTest() {
        Company1CService company1CServiceSpy = spy(company1CService);
        Company1CDTO dto = getCompany1CDTO(123);
        company1CServiceSpy.setCompanyFrom1C(dto);
        verify(company1CServiceSpy, times(1)).updateCompany(dto);
    }


    @Sql(value = "/sql/company/addCompaniesFor1C.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/company/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    void setCompanyTest() {
        Company1CDTO dto = getCompany1CDTO(222);
        company1CService.setCompany(dto);
        Company company = company1CService.getByCode(222);
        assertEquals("new company name", company.getName());
        assertEquals("316316241412", company.getInn());
        assertEquals(5, company.getParent().getId());
    }

    @Sql(value = "/sql/company/addCompaniesFor1C.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/company/after.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Test
    @Transactional
    void updateCompanyTest() {
        Company1CDTO dto = getCompany1CDTO(123);
        company1CService.updateCompany(dto);
        Company company = company1CService.getByCode(123);
        assertEquals("new company name", company.getName());
        assertEquals("316316241412", company.getInn());
        assertEquals(5, company.getParent().getId());
    }

    @NotNull
    private Company1CDTO getCompany1CDTO(int code) {
        Company1CDTO dto = new Company1CDTO();
        dto.setName("new company name");
        dto.setInn("316316241412");
        dto.setCode(code);
        dto.setParentId(321);
        return dto;
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
}
