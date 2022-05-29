package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.model.dto.CompanyDTO;
import com.example.store.model.entities.Company;
import com.example.store.repositories.CompanyRepository;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Value("${our.company.id}")
    private int ourCompanyId;

    public Company getById(int id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_COMPANY_MESSAGE));
    }

    public Company getByName(String name) {
        return companyRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_COMPANY_MESSAGE));
    }

    public List<CompanyDTO> getCompanyDTOList() {
        return companyRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Company getOurCompany() {
        return companyRepository.getById(ourCompanyId);
    }

    public CompanyDTO mapToDTO(Company company) {
        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setMine(company.getId() == ourCompanyId);
        return dto;
    }
}
