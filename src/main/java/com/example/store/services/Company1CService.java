package com.example.store.services;

import com.example.store.model.dto.Company1CDTO;
import com.example.store.model.dto.requests.CompanyList1CRequestDTO;
import com.example.store.model.entities.Company;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class Company1CService extends CompanyService {

    // todo add tests

    public void setCompaniesFrom1C(CompanyList1CRequestDTO companyList1CRequestDTO) {
        List<Company1CDTO> dtoList = companyList1CRequestDTO.getCompany1CDTOList();
        dtoList.sort(Comparator.comparing(Company1CDTO::getCode));
        addRootUsers(dtoList);
        setCompanyRecursive(new ArrayList<>(dtoList));
    }

    protected void addRootUsers(List<Company1CDTO> dtoList) {
        dtoList.stream().filter(dto -> dto.getParentId() == 0).forEach(this::setCompanyFrom1C);
    }

    public void setCompanyFrom1C(Company1CDTO dto) {
        Optional<Company> companyOptional = findByCode(dto.getCode());
        if(companyOptional.isPresent()) {
            updateCompany(dto);
        } else {
            setCompany(dto);
        }
    }

    private void setCompanyRecursive(List<Company1CDTO> dtoList) {
        if(!dtoList.isEmpty()) {
            Iterator<Company1CDTO> iterator = dtoList.iterator();
            boolean interrupt = true;
            while (iterator.hasNext()) {
                Company1CDTO dto = iterator.next();
                if (companyRepository.existsByCode(dto.getParentId())) {
                    setCompanyFrom1C(dto);
                    iterator.remove();
                    interrupt = false;
                }
            }
            if(interrupt) return; // where is not any parent node, so infinity loop
            setCompanyRecursive(dtoList);
        }
    }

    // todo add tests
    public void setCompany(Company1CDTO dto) {
        Company company = companyMapper.mapToItem(dto);
        company.setParent(getByCode(dto.getParentId()));
        companyRepository.save(company);
        if(company.getParent() == null) {
            companyRepository.setParentIdNotNull(company.getId());
        }
    }

    // todo add tests
    public void updateCompany(Company1CDTO dto) {
        Company company = getByCode(dto.getCode());
        if(company == null) return;
        company.setName(dto.getName());
        company.setCode(dto.getCode());
        company.setInn(dto.getInn());
        company.setKpp(dto.getKpp());
        company.setPhone(dto.getPhone());
        company.setEmail(dto.getEmail());
        company.setParent(getByCode(dto.getParentId()));
        companyRepository.save(company);
        if(company.getParent() == null) {
            companyRepository.setParentIdNotNull(company.getId());
        }
    }


}
