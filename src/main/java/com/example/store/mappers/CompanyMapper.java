package com.example.store.mappers;

import com.example.store.model.dto.CompanyDTO;
import com.example.store.model.entities.Company;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Component
public class CompanyMapper extends MappingConverters {

    private final ModelMapper modelMapper;

    @PostConstruct
    public void init() {
        this.modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        this.modelMapper.createTypeMap(Company.class, CompanyDTO.class)
                .addMappings(mapper -> mapper.skip(Company::getAccounts, CompanyDTO::setAccounts));
        this.modelMapper.createTypeMap(CompanyDTO.class, Company.class);
    }

    public CompanyDTO mapToDTO(Company company) {
        return modelMapper.map(company, CompanyDTO.class);
    }
    public Company mapToItem(CompanyDTO dto) {
        return modelMapper.map(dto, Company.class);
    }
}
