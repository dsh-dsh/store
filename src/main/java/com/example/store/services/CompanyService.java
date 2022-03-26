package com.example.store.services;

import com.example.store.exceptions.BadRequestException;
import com.example.store.model.entities.Company;
import com.example.store.repositories.CompanyRepository;
import com.example.store.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public Company getById(int id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(Constants.NO_SUCH_COMPANY_MESSAGE));
    }

    public Company getByName(String name) {
        return companyRepository.findByNameIgnoreCase(name)
                .orElseThrow(BadRequestException::new);
    }
}
